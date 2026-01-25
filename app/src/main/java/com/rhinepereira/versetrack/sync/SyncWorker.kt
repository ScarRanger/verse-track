package com.rhinepereira.versetrack.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rhinepereira.versetrack.data.AppDatabase
import com.rhinepereira.versetrack.data.SupabaseConfig
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.verseDao()

        try {
            // 1. Sync Notes (Themes)
            val unsyncedNotes = dao.getUnsyncedNotes()
            unsyncedNotes.forEach { note ->
                SupabaseConfig.client.postgrest["notes"].upsert(note)
                dao.updateNote(note.copy(isSynced = true))
            }

            // 2. Sync Verses
            val unsyncedVerses = dao.getUnsyncedVerses()
            unsyncedVerses.forEach { verse ->
                SupabaseConfig.client.postgrest["verses"].upsert(verse)
                dao.updateVerse(verse.copy(isSynced = true))
            }

            // 3. Sync Daily Records
            val unsyncedRecords = dao.getUnsyncedDailyRecords()
            unsyncedRecords.forEach { record ->
                SupabaseConfig.client.postgrest["daily_records"].upsert(record)
                dao.updateDailyRecord(record.copy(isSynced = true))
            }

            // 4. Sync Personal Note Categories
            val unsyncedCategories = dao.getUnsyncedCategories()
            unsyncedCategories.forEach { category ->
                SupabaseConfig.client.postgrest["personal_note_categories"].upsert(category)
                dao.insertCategory(category.copy(isSynced = true))
            }

            // 5. Sync Personal Notes
            val unsyncedPersonalNotes = dao.getUnsyncedPersonalNotes()
            unsyncedPersonalNotes.forEach { note ->
                SupabaseConfig.client.postgrest["personal_notes"].upsert(note)
                dao.insertPersonalNote(note.copy(isSynced = true))
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
