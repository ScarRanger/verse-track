package com.rhinepereira.versetrack.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VerseDao {
    @Transaction
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getNotesWithVerses(): Flow<List<NoteWithVerses>>

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM verses WHERE noteId = :noteId ORDER BY createdAt DESC")
    fun getVersesForNote(noteId: String): Flow<List<Verse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerse(verse: Verse)

    @Update
    suspend fun updateNote(note: Note)

    @Update
    suspend fun updateVerse(verse: Verse)

    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteVerse(verse: Verse)

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<Note>

    @Query("SELECT * FROM verses WHERE isSynced = 0")
    suspend fun getUnsyncedVerses(): List<Verse>

    // Daily Records
    @Query("SELECT * FROM daily_records ORDER BY date DESC")
    fun getAllDailyRecords(): Flow<List<DailyRecord>>

    @Query("SELECT * FROM daily_records WHERE date = :dateLimit LIMIT 1")
    fun getRecordForDate(dateLimit: Long): Flow<DailyRecord?>

    // Used for checking existence in ViewModel
    @Query("SELECT * FROM daily_records WHERE date = :dateLimit LIMIT 1")
    suspend fun getRecordForDateSync(dateLimit: Long): DailyRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyRecord(record: DailyRecord)

    @Update
    suspend fun updateDailyRecord(record: DailyRecord)

    @Query("SELECT * FROM daily_records WHERE isSynced = 0")
    suspend fun getUnsyncedDailyRecords(): List<DailyRecord>
}
