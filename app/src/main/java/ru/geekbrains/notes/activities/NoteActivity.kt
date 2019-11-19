package ru.geekbrains.notes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_note.*
import org.jetbrains.anko.alert
import org.koin.android.viewmodel.ext.android.viewModel
import ru.geekbrains.notes.R
import ru.geekbrains.notes.base.BaseActivity
import ru.geekbrains.notes.common.format
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.Data
import ru.geekbrains.notes.viewmodels.NoteViewModel
import java.util.*

class NoteActivity : BaseActivity<Data>() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra"

        fun start(context: Context, noteId: String? = null) =
            Intent(context, NoteActivity::class.java).run {
                this.putExtra(EXTRA_NOTE, noteId)
                context.startActivity(this)
            }

    }

    private var note: Note? = null
    override val layoutResId: Int? = R.layout.activity_note
    override val model: NoteViewModel by viewModel()
    private var color = Note.Color.WHITE

    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            saveNote()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            //Empty
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //Empty
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val noteId = intent.getStringExtra(EXTRA_NOTE)

        noteId?.let {
            model.loadNote(it)
        }

        color_picker.onColorClickListener = {
            color = it
            saveNote()
            initAppBar()
            initView()
        }

        initView()
        initAppBar()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        MenuInflater(this).inflate(R.menu.note, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        R.id.palette -> togglePalette().let { true }
        R.id.delete -> deleteNote().let { true }

        else -> super.onOptionsItemSelected(item)
    }

    private fun togglePalette() {
        if (color_picker.isOpen) {
            color_picker.close()
        } else {
            color_picker.open()
        }
    }

    override fun renderData(data: Data) {
        if (data.isDeleted) {
            finish()
            return
        }
        this.note = data.note
        initView()
        initAppBar()
    }

    private fun saveNote() {
        if (et_note_title.text == null || et_note_title.text!!.length < 3) return

        val title = et_note_title.text.toString()
        val text = et_note_text.text.toString()

        note = note?.copy(
            title = title,
            text = text,
            color = color,
            lastChanged = Date()
        ) ?: Note(UUID.randomUUID().toString(), title, text, color)

        model.save(note!!)
    }

    private fun deleteNote() {
        alert {
            messageResource = R.string.alert_delete_message
            negativeButton(R.string.alert_delete_note_no) { dialog -> dialog.dismiss() }
            positiveButton(R.string.alert_delete_note_ok) { model.deleteNote() }
        }.show()
    }


    private fun initView() {
        et_note_title.removeTextChangedListener(textChangeListener)
        et_note_text.removeTextChangedListener(textChangeListener)
        var inFocus: EditText? = null
        var cursorPosition = 0

        if (et_note_title.hasFocus()) {
            cursorPosition = et_note_title.selectionEnd
            inFocus = et_note_title
        }
        if (et_note_text.hasFocus()) {
            cursorPosition = et_note_text.selectionEnd
            inFocus = et_note_text
        }

        note?.let {
            et_note_title.setText(it.title)
            et_note_text.setText(it.text)
            color = it.color
        }
        inFocus?.setSelection(cursorPosition)

        et_note_title.addTextChangedListener(textChangeListener)
        et_note_text.addTextChangedListener(textChangeListener)

    }

    private fun initAppBar() {
        supportActionBar?.title = note?.lastChanged?.format() ?: getString(R.string.new_note_title)
        hsv_color_picker.setBackgroundColor(
            ContextCompat.getColor(
                this.applicationContext,
                color.id
            )
        )
        toolbar.setBackgroundColor(ContextCompat.getColor(this.applicationContext, color.id))
    }
}
