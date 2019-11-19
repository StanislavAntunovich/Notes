package ru.geekbrains.notes.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import ru.geekbrains.notes.R
import ru.geekbrains.notes.adapters.NotesAdapter
import ru.geekbrains.notes.base.BaseActivity
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.dialogs.LogOutDialog
import ru.geekbrains.notes.viewmodels.MainViewModel

class MainActivity : BaseActivity<List<Note>?>(), LogOutDialog.LogoutListener{

    companion object {
        fun start(context: Context) = Intent(context, MainActivity::class.java).run {
            context.startActivity(this)
        }
    }

    private lateinit var adapter: NotesAdapter

    override val model: MainViewModel by viewModel()

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

    private fun showLogOutDialog() {
        supportFragmentManager.findFragmentByTag(LogOutDialog.TAG) ?: LogOutDialog.create().show(
            supportFragmentManager,
            LogOutDialog.TAG
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?) =
        menuInflater.inflate(R.menu.main, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.logout -> showLogOutDialog().let { true }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onLogOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
    }
}
