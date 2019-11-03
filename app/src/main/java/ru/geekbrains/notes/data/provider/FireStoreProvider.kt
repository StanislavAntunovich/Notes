package ru.geekbrains.notes.data.provider

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import ru.geekbrains.notes.data.NoteResult
import ru.geekbrains.notes.data.entity.Note

class FireStoreProvider : RemoteDataProvider {

    companion object {
        private const val NOTES_COLLECTION = "notes"
    }

    private val store = FirebaseFirestore.getInstance()
    private val notesRef = store.collection(NOTES_COLLECTION)

    override fun subscribeToAll(): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        Timber.d {"getting all"}

        notesRef.addSnapshotListener {snapshot, e ->
            e?.let { result.value = NoteResult.Error(it) }
                ?: let {
                    snapshot?.let {
                        val notes = mutableListOf<Note>()
                        for (doc: QueryDocumentSnapshot in snapshot) {
                            val note: Note = doc.toObject(Note::class.java)
                            notes.add(note)
                        }
                        Timber.d {"got all notes: $notes"}
                        result.value = NoteResult.Success(notes)
                    }
                }
        }
        return result
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        Timber.d {"getting by id: $id"}

        notesRef.document(id).get()
            .addOnSuccessListener {
                val note = it.toObject(Note::class.java)
                result.value = NoteResult.Success(note)
                Timber.d {"getting success with $note"}
            }
            .addOnFailureListener {
                result.value = NoteResult.Error(it)
                Timber.d {"can't get note -> error: ${it.message}"}
            }
        return result
    }

    override fun saveNote(note: Note): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesRef.document(note.id)
            .set(note)
            .addOnSuccessListener {
                result.value = NoteResult.Success(note)
                Timber.d {"saved $note"}
            }
            .addOnFailureListener {
                result.value = NoteResult.Error(it)
                Timber.d {"error saving $note -> ${it.message}"}
            }

        return result
    }
}