package ru.geekbrains.notes.activities

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.geekbrains.notes.R
import ru.geekbrains.notes.adapters.NotesAdapter
import ru.geekbrains.notes.base.BaseActivity
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.MainViewState
import ru.geekbrains.notes.viewmodels.MainViewModel

class MainActivity : BaseActivity<List<Note>?, MainViewState>() {
    private lateinit var adapter: NotesAdapter

    override val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override val layoutResId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)

        rv_notes.layoutManager = GridLayoutManager(this, 2)
        adapter = NotesAdapter {
            NoteActivity.start(this, it.id)
        }

        rv_notes.adapter = adapter

        fab_add.setOnClickListener { NoteActivity.start(this) }
    }

    override fun renderData(data: List<Note>?) {
        data?.let {
            adapter.notes = it
        }
    }
}
