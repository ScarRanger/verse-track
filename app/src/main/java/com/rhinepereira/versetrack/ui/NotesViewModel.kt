package com.rhinepereira.versetrack.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rhinepereira.versetrack.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PersonalNoteRepository
    private val dao: VerseDao

    val categories: StateFlow<List<PersonalNoteCategory>>

    init {
        val database = AppDatabase.getDatabase(application)
        dao = database.verseDao()
        repository = PersonalNoteRepository(application, dao)

        categories = repository.allCategories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Seed predefined categories if empty
        viewModelScope.launch {
            repository.allCategories.first { it.isNotEmpty() || true }.let { current ->
                if (current.isEmpty()) {
                    repository.insertCategory(PersonalNoteCategory(name = "CYP Talks"))
                    repository.insertCategory(PersonalNoteCategory(name = "CGS Talks"))
                    repository.insertCategory(PersonalNoteCategory(name = "Prophecies"))
                }
            }
        }
    }

    fun getNotesForCategory(categoryId: String): Flow<List<PersonalNote>> = repository.getNotesForCategory(categoryId)

    fun addCategory(name: String) {
        viewModelScope.launch {
            repository.insertCategory(PersonalNoteCategory(name = name))
        }
    }

    fun addNote(categoryId: String, title: String, content: String, date: Long) {
        viewModelScope.launch {
            repository.insertNote(PersonalNote(categoryId = categoryId, title = title, content = content, date = date))
        }
    }

    fun updateNote(note: PersonalNote) {
        viewModelScope.launch {
            if (note.id.isBlank() || note.id == "0") { // Check if new
                 repository.insertNote(note.copy(id = java.util.UUID.randomUUID().toString()))
            } else {
                repository.updateNote(note)
            }
        }
    }

    fun deleteNote(note: PersonalNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}
