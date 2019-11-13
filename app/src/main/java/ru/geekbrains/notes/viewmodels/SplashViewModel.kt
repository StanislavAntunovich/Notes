package ru.geekbrains.notes.viewmodels

import ru.geekbrains.notes.base.BaseViewModel
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.exceptions.NoAuthException
import ru.geekbrains.notes.livedata.SplashViewState

class SplashViewModel(private val notesRepository: NotesRepository) : BaseViewModel<Boolean?, SplashViewState>() {
    fun requestUser() {
        notesRepository.getCurrentUser().observeForever {
            viewStateLiveData.value = it?.let {
                SplashViewState(true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }
}