package ru.geekbrains.notes.viewmodels

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import ru.geekbrains.notes.base.BaseViewModel
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.MainViewState

class MainViewModel(notesRepository: NotesRepository) : BaseViewModel<List<Note>?, MainViewState>() {

    private val notesRepo = notesRepository.getNotes()
    private val notesObserver = Observer<NoteResult> { result ->
        result ?: return@Observer

        when (result) {
            is NoteResult.Success<*> -> viewStateLiveData.value =
                MainViewState(result.data as? List<Note>)
            is NoteResult.Error -> viewStateLiveData.value = MainViewState(error = result.error)
        }
    }

    init {
        viewStateLiveData.value = MainViewState()
        notesRepo.observeForever(notesObserver)
    }

    @VisibleForTesting
    public override fun onCleared() {
        notesRepo.removeObserver(notesObserver)
        super.onCleared()
    }


}