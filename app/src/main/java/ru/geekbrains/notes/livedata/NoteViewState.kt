package ru.geekbrains.notes.livedata

import ru.geekbrains.notes.base.BaseViewState
import ru.geekbrains.notes.data.entity.Note

class NoteViewState(note: Note? = null, error: Throwable? = null) : BaseViewState<Note?>(note, error)