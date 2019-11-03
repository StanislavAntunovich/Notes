package ru.geekbrains.notes.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer


abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {
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
        it.message?.let { message ->
            showError(message)
        }
    }

    private fun showError(message: String) = Toast
            .makeText(this, message, Toast.LENGTH_SHORT)
            .show()
}