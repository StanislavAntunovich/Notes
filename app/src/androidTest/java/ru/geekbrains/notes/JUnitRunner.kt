package ru.geekbrains.notes

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class JUnitRunner: AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, AppTest::class.java.name, context)
    }

}