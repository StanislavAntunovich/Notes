package ru.geekbrains.notes.data.provider

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.entity.Note

class FireStoreProviderTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockFirestore = mockk<FirebaseFirestore>()
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockResultCollection = mockk<CollectionReference>()
    private val mockUser = mockk<FirebaseUser>()

    private val mockDocumets = listOf(
        mockk<DocumentSnapshot>(),
        mockk<DocumentSnapshot>(),
        mockk<DocumentSnapshot>()
    )

    private val testNotes = mutableListOf<Note>()

    private val provider = FireStoreProvider(mockAuth, mockFirestore)

    @Before
    fun setUp() {
        mockDocumets.forEachIndexed { index, _ ->
            testNotes.add(Note("$index"))
        }
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns ""
        every { mockFirestore.collection(any()).document(any()).collection(any()) } returns mockResultCollection

        mockDocumets.forEachIndexed { index, doc ->
            every { doc.toObject(Note::class.java) } returns testNotes[index]
        }
    }

    @After
    fun tearDown() {
        testNotes.clear()
        clearAllMocks()
    }

    @Test
    fun subscribeToAll() {
        var result: List<Note>? = null
        val mockSnapshot = mockk<QuerySnapshot>()
        val slot = slot<EventListener<QuerySnapshot>>()

        every { mockSnapshot.documents } returns mockDocumets
        every { mockResultCollection.addSnapshotListener(capture(slot)) } returns mockk()
        provider.subscribeToAll().observeForever {
            result = (it as? NoteResult.Success<List<Note>>)?.data
        }

        slot.captured.onEvent(mockSnapshot, null)
        assertEquals(testNotes, result)
    }

    @Test
    fun getNoteById() {
        var result: Note? = null
        val mockDocumentReference = mockk<DocumentReference>()
        val slot = slot<OnSuccessListener<DocumentSnapshot>>()
        every { mockResultCollection.document(testNotes[0].id) } returns mockDocumentReference
        every { mockDocumentReference.get().addOnSuccessListener(capture(slot)) } returns mockk()

        provider.getNoteById(testNotes[0].id).observeForever {
            result = (it as? NoteResult.Success<Note>)?.data
        }

        slot.captured.onSuccess(mockDocumets[0])
        assertEquals(testNotes[0], result)

    }

    @Test
    fun saveNote() {
        var result: Note? = null
        val mockDocumentReference = mockk<DocumentReference>()
        val slot = slot<OnSuccessListener<in Void>>()
        every { mockResultCollection.document(testNotes[0].id) } returns mockDocumentReference
        every { mockDocumentReference.set(testNotes[0]).addOnSuccessListener(capture(slot)) } returns mockk()

        provider.saveNote(testNotes[0]).observeForever {
            result = (it as? NoteResult.Success<Note>)?.data
        }

        slot.captured.onSuccess(null)
        assertEquals(testNotes[0], result)
    }

    @Test
    fun deleteNote() {
        val mockDocumentReference = mockk<DocumentReference>()
        every { mockResultCollection.document(testNotes[0].id) } returns mockDocumentReference

        provider.deleteNote(testNotes[0].id)
        verify(exactly = 1) { mockDocumentReference.delete() }
    }
}