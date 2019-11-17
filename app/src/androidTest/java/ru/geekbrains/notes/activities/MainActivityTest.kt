package ru.geekbrains.notes.activities

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.stopKoin
import ru.geekbrains.notes.R
import ru.geekbrains.notes.adapters.NotesAdapter
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.MainViewState
import ru.geekbrains.notes.viewmodels.MainViewModel

class MainActivityTest {
    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private val model = mockk<MainViewModel>(relaxed = true)
    private val viewStateLiveData = MutableLiveData<MainViewState>()
    private val testNotes = listOf(
        Note("1", "title1", "text1"),
        Note("2", "title2", "text2"),
        Note("3", "title3", "text3")
    )

    @Before
    fun setUp() {
        StandAloneContext.loadKoinModules(
            listOf(
                module {
                    this.viewModel(override = true) { model }
                }
            )
        )
        every { model.viewState } returns viewStateLiveData
        every { model.onCleared() } just runs

        activityTestRule.launchActivity(null)
        viewStateLiveData.postValue(MainViewState(notes = testNotes))
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun onStart() {
        onView(withId(R.id.rv_notes)).perform(scrollToPosition<NotesAdapter.NotesViewHolder>(1))
        onView(withText(testNotes[1].text)).check(matches(isDisplayed()))
    }
}