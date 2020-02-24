package ru.geekbrains.notes.viewmodels

import kotlinx.coroutines.launch
import ru.geekbrains.notes.base.BaseViewModel
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.exceptions.NoAuthException

class SplashViewModel(private val notesRepository: NotesRepository) : BaseViewModel<Boolean?>() {
    fun requestUser() {
        launch {
            notesRepository.getCurrentUser()?.let {
                setData(true)
            } ?: setError(NoAuthException())
        }
    }
}
