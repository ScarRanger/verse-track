package com.rhinepereira.versetrack.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rhinepereira.versetrack.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class DailyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: VerseRepository
    private val dao: VerseDao
    
    val todayRecord: StateFlow<DailyRecord?>
    val allDailyRecords: StateFlow<List<DailyRecord>>

    init {
        val database = AppDatabase.getDatabase(application)
        dao = database.verseDao()
        repository = VerseRepository(application, dao)
        
        val startOfToday = getStartOfDay(System.currentTimeMillis())
        val endOfToday = startOfToday + (24 * 60 * 60 * 1000)
        
        todayRecord = dao.getRecordForDate(startOfToday, endOfToday).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        allDailyRecords = dao.getAllDailyRecords().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        fetchLatestData()
    }

    private fun fetchLatestData() {
        viewModelScope.launch {
            repository.fetchFromSupabase()
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun updateDailyRecord(
        readToday: Boolean? = null,
        whatRead: String? = null,
        readTime: Int? = null,
        prayedToday: Boolean? = null,
        prayerTime: Int? = null,
        prophecy: String? = null
    ) {
        viewModelScope.launch {
            val startOfToday = getStartOfDay(System.currentTimeMillis())
            val endOfToday = startOfToday + (24 * 60 * 60 * 1000)
            val existing = dao.getRecordForDateSync(startOfToday, endOfToday) ?: DailyRecord(date = startOfToday)
            
            val updated = existing.copy(
                readToday = readToday ?: existing.readToday,
                whatRead = whatRead ?: existing.whatRead,
                totalReadTimeMinutes = readTime ?: existing.totalReadTimeMinutes,
                prayedToday = prayedToday ?: existing.prayedToday,
                totalPrayerTimeMinutes = prayerTime ?: existing.totalPrayerTimeMinutes,
                prophecy = prophecy ?: existing.prophecy,
                isSynced = false
            )
            
            dao.insertDailyRecord(updated)
            repository.scheduleSync()
        }
    }
}
