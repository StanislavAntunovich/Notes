package ru.geekbrains.notes.livedata

import ru.geekbrains.notes.base.BaseViewState

class SplashViewState(authenticated: Boolean? = null, error: Throwable? = null) :
    BaseViewState<Boolean?>(authenticated, error)