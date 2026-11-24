package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dailyCheckDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "senior_daily_check_prefs"
)

interface DailyCheckRepository {
    fun observeToday(todayDateProvider: () -> String = { currentIsoDate() }): Flow<DailyCheckRecord>
    suspend fun ensureTodayDate(todayIso: String = currentIsoDate())
    suspend fun toggleMedication(time: MedicationTime, todayIso: String = currentIsoDate())
    suspend fun addWaterCup(todayIso: String = currentIsoDate())
    suspend fun removeWaterCup(todayIso: String = currentIsoDate())

    companion object {
        fun currentIsoDate(now: Date = Date()): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
        }
    }
}

class DataStoreDailyCheckRepository(
    private val dataStore: DataStore<Preferences>
) : DailyCheckRepository {

    constructor(context: Context) : this(context.applicationContext.dailyCheckDataStore)

    override fun observeToday(todayDateProvider: () -> String): Flow<DailyCheckRecord> {
        return dataStore.data.map { prefs ->
            val todayIso = todayDateProvider()
            val savedDate = prefs[KEY_DATE]
            if (savedDate == todayIso) {
                DailyCheckRecord(
                    dateIso = todayIso,
                    morningTaken = prefs[KEY_MORNING] ?: false,
                    lunchTaken = prefs[KEY_LUNCH] ?: false,
                    dinnerTaken = prefs[KEY_DINNER] ?: false,
                    waterCups = (prefs[KEY_WATER] ?: 0).coerceAtLeast(0)
                )
            } else {
                DailyCheckRecord(dateIso = todayIso)
            }
        }
    }

    override suspend fun ensureTodayDate(todayIso: String) {
        dataStore.edit { prefs ->
            if (prefs[KEY_DATE] != todayIso) {
                resetForNewDay(prefs, todayIso)
            }
        }
    }

    override suspend fun toggleMedication(time: MedicationTime, todayIso: String) {
        dataStore.edit { prefs ->
            if (prefs[KEY_DATE] != todayIso) {
                resetForNewDay(prefs, todayIso)
            }
            val key = when (time) {
                MedicationTime.MORNING -> KEY_MORNING
                MedicationTime.LUNCH -> KEY_LUNCH
                MedicationTime.DINNER -> KEY_DINNER
            }
            val current = prefs[key] ?: false
            prefs[key] = !current
        }
    }

    override suspend fun addWaterCup(todayIso: String) {
        dataStore.edit { prefs ->
            if (prefs[KEY_DATE] != todayIso) {
                resetForNewDay(prefs, todayIso)
            }
            val current = (prefs[KEY_WATER] ?: 0).coerceAtLeast(0)
            prefs[KEY_WATER] = current + 1
        }
    }

    override suspend fun removeWaterCup(todayIso: String) {
        dataStore.edit { prefs ->
            if (prefs[KEY_DATE] != todayIso) {
                resetForNewDay(prefs, todayIso)
            }
            val current = (prefs[KEY_WATER] ?: 0).coerceAtLeast(0)
            prefs[KEY_WATER] = (current - 1).coerceAtLeast(0)
        }
    }

    private fun resetForNewDay(
        prefs: androidx.datastore.preferences.core.MutablePreferences,
        todayIso: String
    ) {
        prefs[KEY_DATE] = todayIso
        prefs[KEY_MORNING] = false
        prefs[KEY_LUNCH] = false
        prefs[KEY_DINNER] = false
        prefs[KEY_WATER] = 0
    }

    companion object {
        private val KEY_DATE = stringPreferencesKey("saved_date")
        private val KEY_MORNING = booleanPreferencesKey("morning_taken")
        private val KEY_LUNCH = booleanPreferencesKey("lunch_taken")
        private val KEY_DINNER = booleanPreferencesKey("dinner_taken")
        private val KEY_WATER = intPreferencesKey("water_cups")
    }
}
