package com.windslash.itriplanery

import com.windslash.itriplanery.data.ItineraryDay
import com.windslash.itriplanery.data.ItineraryStep
import com.windslash.itriplanery.data.TripEntity
import com.windslash.itriplanery.data.TripJson
import com.windslash.itriplanery.data.buildTripExport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/** Verifies the portable trip JSON schema round-trips (export #15 / import #25). */
class TripExportTest {

    @Test
    fun envelopeRoundTrips() {
        val trip = TripEntity(
            id = 1, name = "Japan Mission", destination = "Tokyo", startDate = "Oct 9",
            endDate = "Oct 19", currencyCode = "JPY", budgetAmount = 150000.0, travelerNames = "Jessi & Putra"
        )
        val days = listOf(
            ItineraryDay(
                date = "Oct 9", day = "Day 1", title = "Arrival", location = "Ueno", steps = 0,
                morning = listOf(ItineraryStep("09:00", "Land at Narita", "Arrival", 0, "logistics", id = "s1")),
                afternoon = emptyList(),
                evening = listOf(ItineraryStep("19:00", "Dinner", "Food", 2000, "food", id = "s2"))
            )
        )

        val json = TripJson.toJson(buildTripExport(trip, days))
        val back = TripJson.fromJson(json)

        assertNotNull(back)
        assertEquals(1, back!!.schemaVersion)
        assertEquals("Tokyo", back.trip.destination)
        assertEquals("JPY", back.trip.currencyCode)
        assertEquals(150000.0, back.trip.budgetAmount, 0.001)
        assertEquals(1, back.days.size)
        assertEquals("Land at Narita", back.days[0].morning[0].text)
        assertEquals(2000, back.days[0].evening[0].cost)
    }

    @Test
    fun malformedJsonReturnsNull() {
        assertNull(TripJson.fromJson("this is not json"))
        assertNull(TripJson.fromJson(""))
    }
}
