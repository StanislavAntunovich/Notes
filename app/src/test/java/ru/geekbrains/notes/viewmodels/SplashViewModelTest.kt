package ru.geekbrains.notes.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.notes.data.NotesRepository
import ru.geekbrains.notes.data.entity.User
import ru.geekbrains.notes.data.exceptions.NoAuthException

class SplashViewModelTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository = mockk<NotesRepository>(relaxed = true)
    private val userLiveData = MutableLiveData<User>()
    private val user = User("name", "email")

    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        viewModel = SplashViewModel(mockRepository)
        every { mockRepository.getCurrentUser() } returns userLiveData
    }

    @Test
    fun requestUser() {
        userLiveData.value = user

        viewModel.requestUser()
        verify(exactly = 1) { mockRepository.getCurrentUser() }
        assertTrue(viewModel.viewState.value?.data!!)
    }

    @Test
    fun requestUserError() {
        userLiveData.value = null

        viewModel.requestUser()
        assertTrue(viewModel.viewState.value?.error is NoAuthException)
    }
}