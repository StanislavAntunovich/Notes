package ru.geekbrains.notes.data.provider

import com.github.ajalt.timberkt.Timber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.data.entity.User
import ru.geekbrains.notes.data.exceptions.NoAuthException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FireStoreProvider(
    private val firebaseAuth: FirebaseAuth,
    private val store: FirebaseFirestore
) : RemoteDataProvider {

    companion object {
        private const val NOTES_COLLECTION = "notes"
        private const val USERS_COLLECTION = "users"
    }

    private val currentUser
        get() = firebaseAuth.currentUser

    private fun getUserNotesCollection() = currentUser?.let {
        store.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()


    override fun subscribeToAll(): ReceiveChannel<NoteResult> =
        Channel<NoteResult>(Channel.CONFLATED).apply {
            var listenerRegistration: ListenerRegistration? = null

            try {
                Timber.d { "getting all" }

                listenerRegistration = getUserNotesCollection().addSnapshotListener { snapshot, e ->
                    val value = e?.let { NoteResult.Error(it) }
                        ?: let {
                            snapshot?.let { qs ->
                                val notes = qs.documents.map { it.toObject(Note::class.java) }
                                Timber.d { "got all notes: $notes" }
                                NoteResult.Success(notes)
                            }
                        }
                    value?.let { offer(it) }
                }

            } catch (e: Throwable) {
                offer(NoteResult.Error(e))
            }

            invokeOnClose { listenerRegistration?.remove() }

        }


    override suspend fun getNoteById(id: String): Note = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(id).get()
                .addOnSuccessListener {
                    continuation.resume(it.toObject(Note::class.java)!!)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }

        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }

    }


    override suspend fun saveNote(note: Note): Note = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(note.id)
                .set(note)
                .addOnSuccessListener {
                    continuation.resume(note)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }

        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }

    }

    override suspend fun deleteNote(noteId: String): Unit = suspendCoroutine { continuation ->
        try {
            getUserNotesCollection().document(noteId)
                .delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
        }
    }

    override suspend fun getCurrentUser(): User? = suspendCoroutine { continuation ->
        continuation.resume(currentUser?.let { User(it.displayName ?: "", it.email ?: "") })
    } ?: throw NoAuthException()

}
