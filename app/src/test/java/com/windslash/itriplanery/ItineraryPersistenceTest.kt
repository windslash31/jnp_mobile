package com.windslash.itriplanery

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.windslash.itriplanery.data.AppDatabase
import com.windslash.itriplanery.data.ItineraryData
import com.windslash.itriplanery.data.JapanMissionRepository
import com.windslash.itriplanery.data.StepEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Proves the C1 fix: itinerary steps written to the database survive an app "restart"
 * (database closed and reopened). Before the fix, edits lived only in a StateFlow and
 * were lost on restart.
 */
@RunWith(RobolectricTestRunner::class)
class ItineraryPersistenceTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase("persist_test_db")
    }

    private fun openDb(): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "persist_test_db")
            .allowMainThreadQueries()
            .build()

    @Test
    fun editedStepSurvivesRestart() = runTest {
        // Session 1: user adds a custom step, then the app closes.
        val db1 = openDb()
        db1.itineraryStepDao().upsert(
            StepEntity(
                id = "test-1", dayIndex = 0, period = "morning", time = "09:00",
                text = "My custom plan", meta = "Added", cost = 500, type = "visit",
                details = null, mapQuery = null
            )
        )
        db1.close()

        // Session 2: app reopens — the edit must still be there.
        val db2 = openDb()
        val steps = db2.itineraryStepDao().getAllSteps().first()
        db2.close()

        assertEquals(1, steps.size)
        assertEquals("My custom plan", steps[0].text)
    }

    @Test
    fun seedRunsOnceAndDoesNotDuplicateOnReopen() = runTest {
        // Session 1: seed from the static itinerary, then add one custom step.
        val db1 = openDb()
        val repo1 = JapanMissionRepository(db1)
        repo1.seedStepsIfEmpty(ItineraryData.days)
        val seededCount = db1.itineraryStepDao().count()
        repo1.upsertStep(
            StepEntity("custom-x", 0, "evening", "20:00", "Late ramen", "Added", 800, "food", null, null)
        )
        db1.close()

        // Session 2: seeding again must be a no-op (table not empty) — no duplicates.
        val db2 = openDb()
        val repo2 = JapanMissionRepository(db2)
        repo2.seedStepsIfEmpty(ItineraryData.days)
        val afterCount = db2.itineraryStepDao().count()
        val steps = db2.itineraryStepDao().getAllSteps().first()
        db2.close()

        assertTrue("Seeding should produce steps from the static itinerary", seededCount > 0)
        assertEquals("Only the one custom step should be added; no re-seed", seededCount + 1, afterCount)
        assertTrue("Custom step should persist", steps.any { it.text == "Late ramen" })
    }

    @Test
    fun defaultTripSeedsOnceAndPersists() = runTest {
        // Session 1: seed the default trip, then edit it.
        val db1 = openDb()
        val repo1 = JapanMissionRepository(db1)
        repo1.seedTripIfEmpty()
        val seeded = db1.tripDao().getActiveTrip().first()
        assertEquals("Tokyo, Shinjuku", seeded?.destination)
        assertEquals("JPY", seeded?.currencyCode)
        repo1.updateTrip(seeded!!.copy(destination = "Kyoto", currencyCode = "USD"))
        db1.close()

        // Session 2: reopen — the edit persists and re-seeding is a no-op (count stays 1).
        val db2 = openDb()
        val repo2 = JapanMissionRepository(db2)
        repo2.seedTripIfEmpty()
        val trip = db2.tripDao().getActiveTrip().first()
        val tripCount = db2.tripDao().count()
        db2.close()

        assertEquals(1, tripCount)
        assertEquals("Kyoto", trip?.destination)
        assertEquals("USD", trip?.currencyCode)
    }
}
