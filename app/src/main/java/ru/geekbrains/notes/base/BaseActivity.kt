package ru.geekbrains.notes.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import ru.geekbrains.notes.R
import ru.geekbrains.notes.data.exceptions.NoAuthException


abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {
    companion object {
        private const val SIGN_IN_CODE = 3423
    }

    abstract val viewModel: BaseViewModel<T, S>
    abstract val layoutResId: Int?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutResId?.let {
            setContentView(it)
        }

        viewModel.viewState.observe(this, Observer {
            it ?: return@Observer

            it.error?.let { e ->
                renderError(e)
                return@Observer
            }
            renderData(it.data)
        })
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