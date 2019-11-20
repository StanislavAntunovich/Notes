package ru.geekbrains.notes.viewmodels

import androidx.annotation.VisibleForTesting
import ru.geekbrains.notes.base.BaseViewModel
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.NoteViewState

class NoteViewModel(private val notesRepository: NotesRepository) :
    BaseViewModel<NoteViewState.Data, NoteViewState>() {


    private val pendingNote: Note?
        get() = viewStateLiveData.value?.data?.note

    fun save(note: Note) {
        viewStateLiveData.value = NoteViewState(NoteViewState.Data(note = note))
    }

    fun loadNote(noteId: String) {
        notesRepository.getNoteById(noteId).observeForever { result ->
            result ?: return@observeForever

            when (result) {
                is NoteResult.Success<*> -> viewStateLiveData.value =
                    NoteViewState(NoteViewState.Data(note = result.data as? Note))
                is NoteResult.Error -> viewStateLiveData.value = NoteViewState(error = result.error)
            }
        }
    }

    fun deleteNote() {
        pendingNote?.let { note ->
            notesRepository.deleteNote(note.id).observeForever {result ->
                result?.let {
                    when (it) {
                        is NoteResult.Success<*> -> viewStateLiveData.value = NoteViewState(
                            NoteViewState.Data(true))
                        is NoteResult.Error -> viewStateLiveData.value = NoteViewState(error = it.error)
                    }
                }

            }

        }
    }

    @VisibleForTesting
    public override fun onCleared() {
        pendingNote?.let {
            notesRepository.saveNote(it)
        }
        super.onCleared()
    }

}