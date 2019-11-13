package ru.geekbrains.notes.common

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(format: String = "dd.MM.yyyy HH:mm"): String =
    SimpleDateFormat(format, Locale.getDefault())
        .format(this)
