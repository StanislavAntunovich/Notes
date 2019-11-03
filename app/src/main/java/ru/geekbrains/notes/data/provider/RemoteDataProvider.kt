package ru.geekbrains.notes.data.provider

import androidx.lifecycle.LiveData
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.entity.Note

interface RemoteDataProvider {
    fun subscribeToAll(): LiveData<NoteResult>
    fun getNoteById(id: String): LiveData<NoteResult>
    fun saveNote(note: Note): LiveData<NoteResult>
}