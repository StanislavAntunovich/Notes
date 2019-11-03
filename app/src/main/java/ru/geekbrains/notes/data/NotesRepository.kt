package ru.geekbrains.notes.data

import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.data.provider.FireStoreProvider
import ru.geekbrains.notes.data.provider.RemoteDataProvider

object NotesRepository {
    private val remoteDataProvider: RemoteDataProvider = FireStoreProvider()

    fun getNotes() = remoteDataProvider.subscribeToAll()
    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)

}