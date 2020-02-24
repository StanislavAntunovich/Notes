package ru.geekbrains.notes.data.provider

import kotlinx.coroutines.channels.ReceiveChannel
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.data.entity.User

interface RemoteDataProvider {
    fun subscribeToAll(): ReceiveChannel<NoteResult>
    suspend fun getNoteById(id: String): Note
    suspend fun saveNote(note: Note): Note
    suspend fun getCurrentUser() : User?
    suspend fun deleteNote(noteId: String)
}