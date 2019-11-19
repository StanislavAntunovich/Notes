package ru.geekbrains.notes.livedata

import ru.geekbrains.notes.data.entity.Note

data class Data(val isDeleted: Boolean = false, val note: Note? = null)
