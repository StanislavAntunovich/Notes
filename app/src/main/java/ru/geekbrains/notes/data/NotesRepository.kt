package ru.geekbrains.notes.data

import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.data.provider.RemoteDataProvider

class NotesRepository(private val remoteDataProvider: RemoteDataProvider) {

    fun getNotes() = remoteDataProvider.subscribeToAll()
    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    fun getCurrentUser() = remoteDataProvider.getCurrentUser()
    fun deleteNote(noteId: String) = remoteDataProvider.deleteNote(noteId)

}