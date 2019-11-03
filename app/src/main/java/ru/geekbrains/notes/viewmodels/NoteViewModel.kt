package ru.geekbrains.notes.viewmodels

import ru.geekbrains.notes.base.BaseViewModel
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.NoteViewState

class NoteViewModel : BaseViewModel<Note?, NoteViewState>() {
    init {
        viewStateLiveData.value = NoteViewState()
    }

    private var pendingNote: Note? = null

    fun save(note: Note) {
        pendingNote = note
    }

    fun loadNote(noteId: String) {
        NotesRepository.getNoteById(noteId).observeForever {result ->
            when(result) {
                is NoteResult.Success<*> -> viewStateLiveData.value = NoteViewState(result.data as? Note)
                is NoteResult.Error -> viewStateLiveData.value = NoteViewState(error = result.error)
            }
        }
    }

    override fun onCleared() {
        pendingNote?.let {
            NotesRepository.saveNote(it)
        }
        super.onCleared()
    }

}