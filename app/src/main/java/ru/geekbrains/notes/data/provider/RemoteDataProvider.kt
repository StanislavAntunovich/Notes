package ru.geekbrains.notes.data.provider

import androidx.lifecycle.LiveData
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.data.entity.User

interface RemoteDataProvider {
    fun subscribeToAll(): LiveData<NoteResult>
    fun getNoteById(id: String): LiveData<NoteResult>
    fun saveNote(note: Note): LiveData<NoteResult>
    fun getCurrentUser() : LiveData<User?>
    fun deleteNote(noteId: String) : LiveData<NoteResult>
}