package com.windslash.itriplanery

import com.windslash.itriplanery.data.DayMeta
import com.windslash.itriplanery.data.DayMetaJson
import com.windslash.itriplanery.data.MapMarker
import com.windslash.itriplanery.data.PriorityObjective
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/** Verifies day metadata (incl. nested objectives/markers) round-trips through JSON. */
class DayMetaTest {

    @Test
    fun dayMetaWithNestedTypesRoundTrips() {
        val meta = DayMeta(
            date = "Oct 9", day = "Day 1", title = "Arrival", location = "Ueno", steps = 16000,
            food = PriorityObjective(name = "Kamachiku", type = "Udon", budget = 2000, query = "Kamachiku Nezu", time = "11:30"),
            markers = listOf(MapMarker(lat = 35.71, lng = 139.76, type = "food", seq = 1, title = "Kamachiku", query = "Kamachiku Nezu", meta = "Udon"))
        )

        val back = DayMetaJson.fromJson(DayMetaJson.toJson(meta))

        assertNotNull(back)
        assertEquals("Arrival", back!!.title)
        assertEquals(16000, back.steps)
        assertEquals("Kamachiku", back.food?.name)
        assertEquals(2000, back.food?.budget)
        assertEquals(1, back.markers.size)
        assertEquals(35.71, back.markers[0].lat, 0.001)
        assertEquals("food", back.markers[0].type)
    }
}
