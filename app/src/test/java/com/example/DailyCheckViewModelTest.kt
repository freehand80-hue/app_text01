package com.example

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.data.DataStoreDailyCheckRepository
import com.example.data.MedicationTime
import com.example.ui.DailyCheckViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class DailyCheckViewModelTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `millisUntilNextMidnight computes time to 00_00_00_100 of next day`() {
        val cal = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 17, 23, 59, 50)
            set(Calendar.MILLISECOND, 0)
        }
        val waitMillis = DailyCheckViewModel.millisUntilNextMidnight(cal.time)
        assertEquals(10_100L, waitMillis)
    }

    @Test
    fun `midnight rollover automatically resets yesterday medications and water cups`() = runTest(testDispatcher) {
        val dataStoreFile = File(tempFolder.root, "midnight_rollover_prefs.preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { dataStoreFile }
        )
        val repository = DataStoreDailyCheckRepository(dataStore)

        // Start 10 seconds before midnight on 2026-11-17
        var currentTimeMillis = Calendar.getInstance().apply {
            set(2026, Calendar.NOVEMBER, 17, 23, 59, 50)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val viewModelStore = ViewModelStore()
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DailyCheckViewModel(
                    repository = repository,
                    dateProvider = { Date(currentTimeMillis) }
                ) as T
            }
        }
        val viewModel = ViewModelProvider(viewModelStore, factory)[DailyCheckViewModel::class.java]

        val collectorJob = backgroundScope.launch {
            viewModel.uiState.collect { }
        }
        runCurrent()

        // Mark medications and water cups on 2026-11-17
        viewModel.toggleMedication(MedicationTime.MORNING)
        viewModel.toggleMedication(MedicationTime.LUNCH)
        viewModel.addWaterCup()
        viewModel.addWaterCup()
        runCurrent()

        val beforeMidnight = viewModel.uiState.first { it.isLoaded && it.waterCups == 2 }
        assertTrue(beforeMidnight.morningTaken)
        assertTrue(beforeMidnight.lunchTaken)
        assertEquals(2, beforeMidnight.waterCups)
        assertEquals("2026년 11월 17일", beforeMidnight.formattedDate)

        // Advance clock past midnight into 2026-11-18
        currentTimeMillis += 10_200L
        advanceTimeBy(10_200L)
        runCurrent()

        val afterMidnight = viewModel.uiState.first { it.formattedDate == "2026년 11월 18일" }
        assertFalse(afterMidnight.morningTaken)
        assertFalse(afterMidnight.lunchTaken)
        assertFalse(afterMidnight.dinnerTaken)
        assertEquals(0, afterMidnight.waterCups)

        // Persisted DataStore record should also reflect the new day reset
        val persistedNextDay = repository.observeToday { "2026-11-18" }.first()
        assertFalse(persistedNextDay.morningTaken)
        assertFalse(persistedNextDay.lunchTaken)
        assertFalse(persistedNextDay.dinnerTaken)
        assertEquals(0, persistedNextDay.waterCups)

        collectorJob.cancel()
        viewModelStore.clear()
    }
}
