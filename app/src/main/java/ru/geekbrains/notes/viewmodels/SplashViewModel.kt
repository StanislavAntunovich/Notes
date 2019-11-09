package ru.geekbrains.notes.viewmodels

import ru.geekbrains.notes.base.BaseViewModel
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.exceptions.NoAuthException
import ru.geekbrains.notes.livedata.SplashViewState

class SplashViewModel : BaseViewModel<Boolean?, SplashViewState>() {
    fun requestUser() {
        NotesRepository.getCurrentUser().observeForever {
            viewStateLiveData.value = it?.let {
                SplashViewState(true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }
}