package ru.geekbrains.notes.viewmodels

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import ru.geekbrains.notes.base.BaseViewModel
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.entity.Note

class MainViewModel(notesRepository: NotesRepository) : BaseViewModel<List<Note>?>() {

    private val notesChannel = notesRepository.getNotes()

    init {
        launch {
            notesChannel.consumeEach {
                when (it) {
                    is NoteResult.Success<*> -> setData(it.data as? List<Note>)
                    is NoteResult.Error -> setError(it.error)
                }
            }
        }

    }


    @VisibleForTesting
    public override fun onCleared() {
        notesChannel.cancel()
        super.onCleared()
    }


}