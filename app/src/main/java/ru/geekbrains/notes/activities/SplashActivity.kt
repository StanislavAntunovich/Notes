package ru.geekbrains.notes.activities

import org.koin.android.viewmodel.ext.android.viewModel
import ru.geekbrains.notes.base.BaseActivity
import ru.geekbrains.notes.viewmodels.SplashViewModel

class SplashActivity: BaseActivity<Boolean?>() {
    override val model: SplashViewModel by viewModel()

    override val layoutResId: Int? = null

    override fun onResume() {
        super.onResume()
        model.requestUser()
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let {
            MainActivity.start(this)
        }
    }

}