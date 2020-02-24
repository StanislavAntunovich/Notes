package ru.geekbrains.notes.data

import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.data.provider.RemoteDataProvider

class NotesRepository(private val remoteDataProvider: RemoteDataProvider) {

    fun getNotes() = remoteDataProvider.subscribeToAll()
    suspend fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    suspend fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    suspend fun getCurrentUser() = remoteDataProvider.getCurrentUser()
    suspend fun deleteNote(noteId: String) = remoteDataProvider.deleteNote(noteId)

}