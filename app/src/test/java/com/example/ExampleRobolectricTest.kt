package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.DataStoreDailyCheckRepository
import com.example.data.MedicationTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("약물체크", appName)
    }

    @Test
    fun `datastore repository persists daily state and resets on next day`() = runTest {
        val dataStoreFile = File(tempFolder.root, "test_daily_check.preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { dataStoreFile }
        )
        val repository = DataStoreDailyCheckRepository(dataStore)

        val today = "2026-11-17"
        val tomorrow = "2026-11-18"

        repository.ensureTodayDate(today)
        repository.toggleMedication(MedicationTime.MORNING, today)
        repository.toggleMedication(MedicationTime.DINNER, today)
        repository.addWaterCup(today)
        repository.addWaterCup(today)
        repository.addWaterCup(today)

        val savedToday = repository.observeToday { today }.first()
        assertTrue(savedToday.morningTaken)
        assertFalse(savedToday.lunchTaken)
        assertTrue(savedToday.dinnerTaken)
        assertEquals(3, savedToday.waterCups)

        // Next day automatically resets checks
        val nextDayState = repository.observeToday { tomorrow }.first()
        assertFalse(nextDayState.morningTaken)
        assertFalse(nextDayState.lunchTaken)
        assertFalse(nextDayState.dinnerTaken)
        assertEquals(0, nextDayState.waterCups)
    }

    @Test
    fun `senior daily check flow updates medications and water cups`() {
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("today_date_text").assertIsDisplayed()
        composeRule.onNodeWithTag("morning_med_button").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("lunch_med_button").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("dinner_med_button").performScrollTo().assertIsDisplayed()

        // Tap morning medicine button -> shows 복용 완료, celebration not yet visible
        composeRule.onNodeWithTag("morning_med_button").performScrollTo().performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasText("복용 완료")
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("복용 완료").assertIsDisplayed()
        assertTrue(
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasText("오늘도 건강 챙기기 성공!")
            ).fetchSemanticsNodes().isEmpty()
        )

        // Tap lunch & dinner medicine buttons -> celebration banner appears
        composeRule.onNodeWithTag("lunch_med_button").performScrollTo().performClick()
        composeRule.onNodeWithTag("dinner_med_button").performScrollTo().performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasText("오늘도 건강 챙기기 성공!")
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("med_celebration_banner").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("오늘도 건강 챙기기 성공!").assertIsDisplayed()

        // Scroll to and tap add water button twice -> counter updates to 2 잔
        composeRule.onNodeWithTag("add_water_button").performScrollTo().performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasTestTag("water_count_text") and
                    androidx.compose.ui.test.hasText("1 잔")
            ).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("add_water_button").performScrollTo().performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasTestTag("water_count_text") and
                    androidx.compose.ui.test.hasText("2 잔")
            ).fetchSemanticsNodes().isNotEmpty()
        }

        // Undo 1 cup -> counter updates to 1 잔
        composeRule.onNodeWithTag("undo_water_button").performScrollTo().performClick()
        composeRule.waitUntil(timeoutMillis = 3_000) {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasTestTag("water_count_text") and
                    androidx.compose.ui.test.hasText("1 잔")
            ).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
