package ru.geekbrains.notes.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.CoroutineContext

open class BaseViewModel<T> : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Default + Job()
    }

    private val dataChannel = BroadcastChannel<T>(Channel.CONFLATED)
    private val errorChannel = Channel<Throwable>()

    val data: ReceiveChannel<T>
        get() = dataChannel.openSubscription()


    val errors: ReceiveChannel<Throwable>
        get() = errorChannel

    fun setData(data: T) {
        launch {
            dataChannel.send(data)
        }
    }

    fun setError(e: Throwable) {
        launch {
            errorChannel.send(e)
        }
    }

    override fun onCleared() {
        dataChannel.close()
        errorChannel.close()
        coroutineContext.cancel()
        super.onCleared()
    }


}