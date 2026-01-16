package com.rhinepereira.versetrack.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rhinepereira.versetrack.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VerseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VerseRepository
    val allNotesWithVerses: StateFlow<List<NoteWithVerses>>

    init {
        val verseDao = AppDatabase.getDatabase(application).verseDao()
        repository = VerseRepository(application, verseDao)
        allNotesWithVerses = repository.allNotesWithVerses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun getVersesForNote(noteId: String): Flow<List<Verse>> = repository.getVersesForNote(noteId)

    fun addNote(theme: String) {
        viewModelScope.launch {
            repository.insertNote(Note(theme = theme))
        }
    }

    fun addVerse(noteId: String, reference: String, content: String) {
        viewModelScope.launch {
            repository.insertVerse(Verse(noteId = noteId, reference = reference, content = content))
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun deleteVerse(verse: Verse) {
        viewModelScope.launch {
            repository.deleteVerse(verse)
        }
    }
}
