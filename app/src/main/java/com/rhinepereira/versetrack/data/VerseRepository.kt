package com.rhinepereira.versetrack.data

import android.content.Context
import androidx.work.*
import com.rhinepereira.versetrack.sync.SyncWorker
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VerseRepository(private val context: Context, private val verseDao: VerseDao) {

    val allNotesWithVerses: Flow<List<NoteWithVerses>> = verseDao.getNotesWithVerses()

    fun getVersesForNote(noteId: String): Flow<List<Verse>> = verseDao.getVersesForNote(noteId)

    suspend fun insertNote(note: Note) {
        verseDao.insertNote(note.copy(isSynced = false))
        scheduleSync()
    }

    suspend fun insertVerse(verse: Verse) {
        verseDao.insertVerse(verse.copy(isSynced = false))
        scheduleSync()
    }

    suspend fun updateVerse(verse: Verse) {
        verseDao.updateVerse(verse.copy(isSynced = false))
        scheduleSync()
    }

    suspend fun deleteNote(note: Note) {
        verseDao.deleteNote(note)
        withContext(Dispatchers.IO) {
            try {
                SupabaseConfig.client.postgrest["notes"].delete {
                    filter { eq("id", note.id) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteVerse(verse: Verse) {
        verseDao.deleteVerse(verse)
        withContext(Dispatchers.IO) {
            try {
                SupabaseConfig.client.postgrest["verses"].delete {
                    filter { eq("id", verse.id) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                java.util.concurrent.TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "supabase_sync",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }
}
