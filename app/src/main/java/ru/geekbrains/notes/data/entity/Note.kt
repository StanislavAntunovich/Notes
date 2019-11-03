package ru.geekbrains.notes.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import ru.geekbrains.notes.R
import java.util.*

@Parcelize
data class Note(
        val id: String = "",
        val title: String = "",
        val text: String = "",
        val color: Color = Color.WHITE,
        val lastChanged: Date = Date()
) : Parcelable {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Note
        if (id != other.id) return false
        return true
    }


    enum class Color(val id: Int) {
        WHITE(R.color.white),
        RED(R.color.red),
        PINK(R.color.pink),
        GREEN(R.color.green),
        VIOLET(R.color.violet);
    }
}
