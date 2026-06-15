package com.windslash.itriplanery

import com.windslash.itriplanery.data.ItineraryDay
import com.windslash.itriplanery.data.ItineraryStep
import com.windslash.itriplanery.data.deriveFoodCategories
import com.windslash.itriplanery.data.itineraryProgress
import org.junit.Assert.assertEquals
import org.junit.Test

/** Pure unit tests for the checklist progress calculation (covers the #9 B3/B4 fixes). */
class ItineraryProgressTest {

    private fun step(id: String) =
        ItineraryStep(time = "09:00", text = "x", meta = "m", cost = 0, type = "visit", id = id)

    private fun day(
        morning: List<ItineraryStep> = emptyList(),
        afternoon: List<ItineraryStep> = emptyList(),
        evening: List<ItineraryStep> = emptyList(),
        customAlts: List<ItineraryStep> = emptyList()
    ) = ItineraryDay(
        date = "", day = "", title = "", location = "", steps = 0,
        morning = morning, afternoon = afternoon, evening = evening, customAlts = customAlts
    )

    @Test
    fun emptyItineraryIsZero() {
        assertEquals(0, itineraryProgress(emptyList(), emptyMap()))
        assertEquals(0, itineraryProgress(listOf(day()), emptyMap()))
    }

    @Test
    fun halfCheckedIsFiftyPercent() {
        val d = day(morning = listOf(step("a"), step("b")), evening = listOf(step("c"), step("d")))
        val checks = mapOf("a" to true, "c" to true)
        assertEquals(50, itineraryProgress(listOf(d), checks))
    }

    @Test
    fun alternativeStepsCountTowardProgress() {
        // Regression for B3: alternatives were previously ignored in the denominator.
        val d = day(morning = listOf(step("a")), customAlts = listOf(step("alt1")))
        // Only the alternative is checked -> 1 of 2 = 50%.
        assertEquals(50, itineraryProgress(listOf(d), mapOf("alt1" to true)))
        // Both checked -> 100%.
        assertEquals(100, itineraryProgress(listOf(d), mapOf("a" to true, "alt1" to true)))
    }

    @Test
    fun keyedByStableIdNotPosition() {
        // A check for an id that isn't present must not count (no positional aliasing).
        val d = day(morning = listOf(step("real")))
        assertEquals(0, itineraryProgress(listOf(d), mapOf("d0-m-0" to true)))
        assertEquals(100, itineraryProgress(listOf(d), mapOf("real" to true)))
    }

    @Test
    fun allCompleteAcrossMultipleDays() {
        val d1 = day(morning = listOf(step("a")), afternoon = listOf(step("b")))
        val d2 = day(evening = listOf(step("c")))
        val checks = mapOf("a" to true, "b" to true, "c" to true)
        assertEquals(100, itineraryProgress(listOf(d1, d2), checks))
    }

    @Test
    fun foodCategoriesPicksOnlyFoodTypeSteps() {
        val d = day(
            morning = listOf(
                ItineraryStep("09:00", "Sushi Bar", "Lunch", 2000, "food", id = "a"),
                ItineraryStep("10:00", "Museum", "", 0, "sightseeing", id = "b")
            ),
            evening = listOf(ItineraryStep("19:00", "Ramen", "Dinner", 1000, "food", id = "c"))
        )
        val cats = deriveFoodCategories(listOf(d))
        assertEquals(1, cats.size)
        assertEquals(2, cats[0].items.size) // food steps only, not the museum
        assertEquals(setOf("Sushi Bar", "Ramen"), cats[0].items.map { it.name }.toSet())
    }

    @Test
    fun foodCategoriesEmptyWhenNoFood() {
        val d = day(morning = listOf(ItineraryStep("09:00", "Museum", "", 0, "sightseeing", id = "b")))
        assertEquals(0, deriveFoodCategories(listOf(d)).size)
    }
}
