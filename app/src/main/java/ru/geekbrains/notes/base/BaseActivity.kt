package ru.geekbrains.notes.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import ru.geekbrains.notes.R
import ru.geekbrains.notes.data.exceptions.NoAuthException
import kotlin.coroutines.CoroutineContext


abstract class BaseActivity<T> : AppCompatActivity(), CoroutineScope {
    companion object {
        private const val SIGN_IN_CODE = 3423
    }

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + Job()
    }

    abstract val model: BaseViewModel<T>
    abstract val layoutResId: Int?

    private lateinit var dataJob: Job
    private lateinit var errorJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutResId?.let {
            setContentView(it)
        }
    }

    override fun onStart() {
        super.onStart()

        dataJob = launch {
            model.data.consumeEach {
                renderData(it)
            }
        }

        errorJob = launch {
            model.errors.consumeEach {
                renderError(it)
            }
        }
    }

    override fun onStop() {
        dataJob.cancel()
        errorJob.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        super.onDestroy()
    }

    abstract fun renderData(data: T)


    private fun renderError(e: Throwable?) = e?.let {
        when (it) {
            is NoAuthException -> logIn()
            else -> it.message?.let { message ->
                showError(message)
            }
        }
    }

    private fun showError(message: String) = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .show()

    private fun logIn() {
        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.bender)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .build()

            , SIGN_IN_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE && resultCode != Activity.RESULT_OK) {
            finish()
        }
    }
}