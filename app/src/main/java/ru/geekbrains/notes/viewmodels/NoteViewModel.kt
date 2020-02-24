package ru.geekbrains.notes.viewmodels

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.launch
import ru.geekbrains.notes.base.BaseViewModel
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.Data

class NoteViewModel(private val notesRepository: NotesRepository) :
    BaseViewModel<Data>() {


    private val currentNote: Note?
        get() = data.poll()?.note

    fun save(note: Note) {
        setData(Data(note = note))
    }

    fun loadNote(noteId: String) {
        launch {
            try {
                notesRepository.getNoteById(noteId).let {
                    setData(Data(note = it))
                }
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    fun deleteNote() {
        launch {
            try {
                currentNote?.let {
                    notesRepository.deleteNote(it.id)
                }
                setData(Data(true))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    @VisibleForTesting
    public override fun onCleared() {
        launch {
            currentNote?.let { notesRepository.saveNote(it) }
            super.onCleared()
        }
    }

}