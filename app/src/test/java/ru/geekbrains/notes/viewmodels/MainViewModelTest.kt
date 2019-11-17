package ru.geekbrains.notes.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.entity.Note

class MainViewModelTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository = mockk<NotesRepository>(relaxed = true)
    private val notesLiveData = MutableLiveData<NoteResult>()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        every { mockRepository.getNotes() } returns notesLiveData
        viewModel = MainViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun onStart() {
        verify(exactly = 1) { mockRepository.getNotes() }
    }

    @Test
    fun onOk(){
        var result: List<Note>? = null
        val testData = listOf(Note("1"), Note("2"))
        viewModel.viewState.observeForever {
            result = it?.data
        }
        notesLiveData.value = NoteResult.Success(testData)
        assertEquals(testData, result)
    }

    @Test
    fun onError(){
        var result: Throwable? = null
        val testData = Throwable("error")
        viewModel.viewState.observeForever {
            result = it?.error
        }
        notesLiveData.value = NoteResult.Error(error = testData)
        assertEquals(testData, result)
    }

    @Test
    fun onCleared(){
        viewModel.onCleared()
        assertFalse(notesLiveData.hasObservers())
    }
}