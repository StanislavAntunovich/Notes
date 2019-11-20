package ru.geekbrains.notes.activities

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.StandAloneContext.stopKoin
import ru.geekbrains.notes.R
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.livedata.NoteViewState
import ru.geekbrains.notes.viewmodels.NoteViewModel

class NoteActivityTest {
    @get:Rule
    val activityTestRule = ActivityTestRule(NoteActivity::class.java, true, false)

    private val model = mockk<NoteViewModel>(relaxed = true)
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private val testNote = Note("1", "title1", "text1")

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
        viewStateLiveData.postValue(NoteViewState(data = NoteViewState.Data(note = testNote)))

    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun onStart() {
        onView(withId(R.id.et_note_text)).check(matches(withText(testNote.text)))
    }

    @Test
    fun onPaletteClick() {
        onView(withId(R.id.palette)).perform(click())
        onView(withId(R.id.color_picker)).check(matches(isDisplayed()))
    }

    @Test
    fun onTextChanged() {
        onView(withId(R.id.et_note_text)).perform(typeText("more than 3"))
        verify(atLeast = 1) { model.save(any()) }
    }

}