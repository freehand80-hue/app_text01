package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.DailyCheckRecord
import com.example.data.DailyCheckRepository
import com.example.data.MedicationTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyCheckUiState(
    val formattedDate: String = "",
    val formattedDayOfWeek: String = "",
    val morningTaken: Boolean = false,
    val lunchTaken: Boolean = false,
    val dinnerTaken: Boolean = false,
    val waterCups: Int = 0,
    val waterGoal: Int = 8,
    val isLoaded: Boolean = false
) {
    val takenMedicationCount: Int
        get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DailyCheckViewModel(
    private val repository: DailyCheckRepository,
    private val dateProvider: () -> Date = { Date() }
) : ViewModel() {

    private val currentDateTrigger = MutableStateFlow(currentIso())

    val uiState: StateFlow<DailyCheckUiState> = currentDateTrigger
        .flatMapLatest { isoDate ->
            repository.observeToday { isoDate }
                .combine(currentDateTrigger) { record, _ ->
                    val now = dateProvider()
                    record.toUiState(now)
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DailyCheckUiState(
                formattedDate = formatKoreanDate(dateProvider()),
                formattedDayOfWeek = formatKoreanDayOfWeek(dateProvider()),
                isLoaded = false
            )
        )

    init {
        refreshDate()
        scheduleMidnightRollover()
    }

    private fun scheduleMidnightRollover() {
        viewModelScope.launch {
            while (isActive) {
                val waitMillis = millisUntilNextMidnight(dateProvider())
                delay(waitMillis)
                refreshDate()
            }
        }
    }

    fun refreshDate() {
        val todayIso = currentIso()
        currentDateTrigger.value = todayIso
        viewModelScope.launch {
            repository.ensureTodayDate(todayIso)
        }
    }

    fun toggleMedication(time: MedicationTime) {
        val todayIso = currentIso()
        currentDateTrigger.value = todayIso
        viewModelScope.launch {
            repository.toggleMedication(time, todayIso)
        }
    }

    fun addWaterCup() {
        val todayIso = currentIso()
        currentDateTrigger.value = todayIso
        viewModelScope.launch {
            repository.addWaterCup(todayIso)
        }
    }

    fun removeWaterCup() {
        val todayIso = currentIso()
        currentDateTrigger.value = todayIso
        viewModelScope.launch {
            repository.removeWaterCup(todayIso)
        }
    }

    private fun currentIso(): String = DailyCheckRepository.currentIsoDate(dateProvider())

    private fun DailyCheckRecord.toUiState(now: Date): DailyCheckUiState {
        return DailyCheckUiState(
            formattedDate = formatKoreanDate(now),
            formattedDayOfWeek = formatKoreanDayOfWeek(now),
            morningTaken = morningTaken,
            lunchTaken = lunchTaken,
            dinnerTaken = dinnerTaken,
            waterCups = waterCups,
            waterGoal = 8,
            isLoaded = true
        )
    }

    companion object {
        fun formatKoreanDate(date: Date): String {
            return SimpleDateFormat("yyyy년 M월 d일", Locale.KOREA).format(date)
        }

        fun formatKoreanDayOfWeek(date: Date): String {
            return SimpleDateFormat("EEEE", Locale.KOREA).format(date)
        }

        fun millisUntilNextMidnight(now: Date): Long {
            val nextMidnight = Calendar.getInstance().apply {
                time = now
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 100)
            }
            return (nextMidnight.timeInMillis - now.time).coerceAtLeast(250L)
        }

        fun factory(repository: DailyCheckRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DailyCheckViewModel(repository) as T
                }
            }
        }
    }
}
