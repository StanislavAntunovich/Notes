package ru.geekbrains.notes.data.provider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.entity.Note
import ru.geekbrains.notes.data.entity.User
import ru.geekbrains.notes.data.exceptions.NoAuthException

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

    private fun getUserNotesCollection() =
        currentUser?.let {
            store.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION)
        } ?: throw NoAuthException()

    override fun subscribeToAll(): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                Timber.d { "getting all" }

                getUserNotesCollection().addSnapshotListener { snapshot, e ->
                    e?.let { value = NoteResult.Error(it) }
                        ?: let {
                            snapshot?.let {
                                val notes = mutableListOf<Note>()
                                for (doc: QueryDocumentSnapshot in snapshot) {
                                    val note: Note = doc.toObject(Note::class.java)
                                    notes.add(note)
                                }
                                Timber.d { "got all notes: $notes" }
                                value = NoteResult.Success(notes)
                            }
                        }
                }

            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }


    override fun getNoteById(id: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                Timber.d { "getting by id: $id" }

                getUserNotesCollection().document(id).get()
                    .addOnSuccessListener {
                        val note = it.toObject(Note::class.java)
                        value = NoteResult.Success(note)
                        Timber.d { "getting success with $note" }
                    }
                    .addOnFailureListener {
                        value = NoteResult.Error(it)
                        Timber.d { "can't get note -> error: ${it.message}" }
                    }

            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }


    override fun saveNote(note: Note): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(note.id)
                    .set(note)
                    .addOnSuccessListener {
                        value = NoteResult.Success(note)
                        Timber.d { "saved $note" }
                    }
                    .addOnFailureListener {
                        value = NoteResult.Error(it)
                        Timber.d { "error saving $note -> ${it.message}" }
                    }

            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }

    override fun deleteNote(noteId: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            try {
                getUserNotesCollection().document(noteId)
                    .delete()
                    .addOnSuccessListener {
                        value = NoteResult.Success(null)
                    }
                    .addOnFailureListener {
                        value = NoteResult.Error(it)
                    }
            } catch (e: Throwable) {
                value = NoteResult.Error(e)
            }
        }


    override fun getCurrentUser(): LiveData<User?> =
        MutableLiveData<User?>().apply {
            value = currentUser?.let {
                User(it.displayName ?: "", it.email ?: "")
            }
        }

}
