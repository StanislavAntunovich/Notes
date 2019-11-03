package ru.geekbrains.notes

import android.app.Application
import com.github.ajalt.timberkt.Timber

class NoteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(timber.log.Timber.DebugTree())
    }
}