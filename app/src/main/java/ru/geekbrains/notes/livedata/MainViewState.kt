package ru.geekbrains.notes.livedata

import ru.geekbrains.notes.base.BaseViewState
import ru.geekbrains.notes.data.entity.Note

class MainViewState(notes: List<Note>? = null, error: Throwable? = null) : BaseViewState<List<Note>?>(notes, error)
