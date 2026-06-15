package com.windslash.itriplanery

import com.windslash.itriplanery.data.ContingencyPlan
import com.windslash.itriplanery.data.ItineraryDay
import com.windslash.itriplanery.data.ItineraryStep
import com.windslash.itriplanery.data.MapMarker
import com.windslash.itriplanery.data.PriorityObjective
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
                date = "Oct 9", day = "Day 1", title = "Arrival", location = "Ueno", steps = 16000,
                morning = listOf(ItineraryStep("09:00", "Land at Narita", "Arrival", 0, "logistics", id = "s1")),
                afternoon = emptyList(),
                evening = listOf(ItineraryStep("19:00", "Dinner", "Food", 2000, "food", id = "s2")),
                customAlts = listOf(ItineraryStep("15:00", "Backup plan", "Alt", 0, "visit", id = "s3")),
                food = PriorityObjective("Kamachiku", "Udon", 2000, "Kamachiku Nezu", "11:30"),
                alts = listOf(ContingencyPlan("Rainy day", 0, "Indoor museum", "Tokyo Museum")),
                markers = listOf(MapMarker(35.71, 139.76, "food", 1, "Kamachiku", "Kamachiku Nezu", "Udon"))
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
        val d = back.days[0]
        assertEquals("Day 1", d.day)
        assertEquals(16000, d.steps)
        assertEquals("Land at Narita", d.morning[0].text)
        assertEquals(2000, d.evening[0].cost)
        // The previously-dropped rich fields now survive the round-trip:
        assertEquals("Backup plan", d.alternatives[0].text)
        assertEquals("Kamachiku", d.food?.name)
        assertEquals(1, d.contingencies.size)
        assertEquals("Rainy day", d.contingencies[0].name)
        assertEquals(1, d.markers.size)
        assertEquals(35.71, d.markers[0].lat, 0.001)
    }

    @Test
    fun malformedJsonReturnsNull() {
        assertNull(TripJson.fromJson("this is not json"))
        assertNull(TripJson.fromJson(""))
    }
}
