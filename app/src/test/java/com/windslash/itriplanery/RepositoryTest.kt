package com.windslash.itriplanery

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.windslash.itriplanery.data.AppDatabase
import com.windslash.itriplanery.data.DayMeta
import com.windslash.itriplanery.data.ItineraryData
import com.windslash.itriplanery.data.JapanMissionRepository
import com.windslash.itriplanery.data.StepEntity
import com.windslash.itriplanery.data.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Data-layer tests for the repository (transactions, checks, step CRUD). */
@RunWith(RobolectricTestRunner::class)
class RepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repo: JapanMissionRepository

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java).allowMainThreadQueries().build()
        repo = JapanMissionRepository(db)
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun transactionsInsertSumAndDelete() = runTest {
        repo.insertTransaction(TransactionEntity(desc = "Ramen", amount = 1000.0, category = "Food", date = "Oct 9", time = "12:00"))
        repo.insertTransaction(TransactionEntity(desc = "Train", amount = 500.0, category = "Transport", date = "Oct 9", time = "09:00"))

        val all = repo.allTransactions.first()
        assertEquals(2, all.size)
        assertEquals(1500.0, all.sumOf { it.amount }, 0.001)

        // Ordered by id DESC, so all.first() is the most recent (Train, 500). Deleting it
        // leaves Ramen (1000).
        repo.deleteTransaction(all.first().id)
        val afterDelete = repo.allTransactions.first()
        assertEquals(1, afterDelete.size)
        assertEquals(1000.0, afterDelete.single().amount, 0.001)
    }

    @Test
    fun itineraryCheckTogglesOnAndOff() = runTest {
        repo.toggleItineraryCheck("d0-m-0", true)
        assertEquals(true, repo.allItineraryChecks.first().single { it.key == "d0-m-0" }.isCompleted)

        repo.toggleItineraryCheck("d0-m-0", false)
        assertEquals(false, repo.allItineraryChecks.first().single { it.key == "d0-m-0" }.isCompleted)
    }

    @Test
    fun foodCheckTogglesOnAndOff() = runTest {
        repo.toggleFoodCheck("f_kamo", true)
        assertEquals(true, repo.allFoodChecks.first().single { it.itemId == "f_kamo" }.isCompleted)

        repo.toggleFoodCheck("f_kamo", false)
        assertEquals(false, repo.allFoodChecks.first().single { it.itemId == "f_kamo" }.isCompleted)
    }

    @Test
    fun stepUpsertUpdatesInPlaceThenDeletes() = runTest {
        val s = StepEntity(
            id = "s1", dayIndex = 0, period = "morning", time = "09:00",
            text = "Original", meta = "m", cost = 0, type = "visit", details = null, mapQuery = null
        )
        repo.upsertStep(s)
        assertEquals(1, repo.allSteps.first().size)

        // Same id -> update, not duplicate insert.
        repo.upsertStep(s.copy(text = "Updated"))
        val steps = repo.allSteps.first()
        assertEquals(1, steps.size)
        assertEquals("Updated", steps.first().text)

        repo.deleteStep("s1")
        assertEquals(0, repo.allSteps.first().size)
    }

    @Test
    fun replaceItinerarySwapsDaysAndSteps() = runTest {
        // Seed the original itinerary + a stray step.
        repo.seedDaysIfEmpty(ItineraryData.days)
        repo.upsertStep(StepEntity("old", 0, "morning", "08:00", "Old step", "m", 0, "visit", null, null))

        // Import replaces everything with a single new day + step.
        val metas = listOf(DayMeta(date = "D1", day = "Day 1", title = "Imported Day", location = "Somewhere", steps = 0))
        val steps = listOf(listOf(StepEntity("n1", 0, "morning", "09:00", "Imported step", "m", 0, "visit", null, null)))
        repo.replaceItinerary(metas, steps)

        val days = repo.allDays.first()
        val allSteps = repo.allSteps.first()
        assertEquals(1, days.size)
        assertEquals("Imported Day", days[0].title)
        assertEquals(1, allSteps.size)
        assertEquals("Imported step", allSteps[0].text)
    }
}
