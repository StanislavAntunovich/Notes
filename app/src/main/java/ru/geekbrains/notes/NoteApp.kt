package ru.geekbrains.notes

import android.app.Application
import com.github.ajalt.timberkt.Timber
import org.koin.android.ext.android.startKoin
import ru.geekbrains.notes.di.appModule
import ru.geekbrains.notes.di.mainModule
import ru.geekbrains.notes.di.noteModule
import ru.geekbrains.notes.di.splashModule

class NoteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(timber.log.Timber.DebugTree())
        startKoin(this, listOf(appModule, splashModule, mainModule, noteModule))
    }
}