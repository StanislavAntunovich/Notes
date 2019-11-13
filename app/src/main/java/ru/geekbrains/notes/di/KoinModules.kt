package ru.geekbrains.notes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.provider.FireStoreProvider
import ru.geekbrains.notes.data.provider.RemoteDataProvider
import ru.geekbrains.notes.viewmodels.MainViewModel
import ru.geekbrains.notes.viewmodels.NoteViewModel
import ru.geekbrains.notes.viewmodels.SplashViewModel

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single<RemoteDataProvider> { FireStoreProvider(get(), get()) }
    single { NotesRepository(get()) }
}

val splashModule = module {
    viewModel { SplashViewModel(get()) }
}

val mainModule = module {
    viewModel { MainViewModel(get()) }
}

val noteModule = module {
    viewModel { NoteViewModel(get()) }
}