package ru.geekbrains.notes.activities

import org.koin.android.viewmodel.ext.android.viewModel
import ru.geekbrains.notes.base.BaseActivity
import ru.geekbrains.notes.livedata.SplashViewState
import ru.geekbrains.notes.viewmodels.SplashViewModel

class SplashActivity: BaseActivity<Boolean?, SplashViewState>() {
    override val viewModel: SplashViewModel by viewModel()

    override val layoutResId: Int? = null

    override fun onResume() {
        super.onResume()
        viewModel.requestUser()
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let {
            MainActivity.start(this)
        }
    }

}