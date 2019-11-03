package ru.geekbrains.notes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_note.view.*
import ru.geekbrains.notes.R
import ru.geekbrains.notes.data.entity.Note

class NotesAdapter(val onItemClick: ((Note) -> Unit)? = null) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    var notes = listOf<Note>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NotesViewHolder(LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_note, parent, false))


    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bindNote(notes[position])
    }


    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindNote(note: Note) {
            with(itemView) {
                tv_title.text = note.title
                tv_text.text = note.text
                setBackgroundColor(getColor(itemView.context, note.color.id))

                itemView.setOnClickListener {
                    onItemClick?.invoke(note)
                }
            }
        }
    }
}