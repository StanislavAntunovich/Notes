package ru.geekbrains.notes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_note.*
import ru.geekbrains.notes.R
import ru.geekbrains.notes.base.BaseActivity
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.NoteViewState
import ru.geekbrains.notes.viewmodels.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra"
        private const val DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm"

        fun start(context: Context, noteId: String? = null)  =
             Intent(context, NoteActivity::class.java).run {
                this.putExtra(EXTRA_NOTE, noteId)
                context.startActivity(this)
            }

    }

    private var note: Note? = null
    override val layoutResId: Int? = R.layout.activity_note
    override val viewModel: NoteViewModel by lazy {
        ViewModelProviders.of(this).get(NoteViewModel::class.java)
    }
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

        noteId?.let { viewModel.loadNote(it) }

        setAppBarTitle()

        initView()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun renderData(data: Note?) {
        this.note = data
        setAppBarTitle()
        initView()
    }

    private fun saveNote() {
        if (et_note_title.text == null || et_note_title.text!!.length < 3) return

        val title = et_note_title.text.toString()
        val text = et_note_text.text.toString()

        note = note?.copy(
            title = title,
            text = text,
            lastChanged = Date()
        ) ?: Note(UUID.randomUUID().toString(), title, text)

        viewModel.save(note!!)
    }

    private fun initView() {
        et_note_title.removeTextChangedListener(textChangeListener)
        et_note_text.removeTextChangedListener(textChangeListener)

        note?.let {
            et_note_title.setText(it.title)
            et_note_text.setText(it.text)

            toolbar.setBackgroundColor(ContextCompat.getColor(this.applicationContext, it.color.id))
        }

        et_note_title.addTextChangedListener(textChangeListener)
        et_note_text.addTextChangedListener(textChangeListener)
    }

    private fun setAppBarTitle() {
        supportActionBar?.title = note?.let {
            SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(it.lastChanged)
        } ?: getString(R.string.new_note_title)
    }
}
