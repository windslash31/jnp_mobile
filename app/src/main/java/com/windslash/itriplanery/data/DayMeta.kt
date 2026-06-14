package com.windslash.itriplanery.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * The non-step metadata for one itinerary day. Steps (morning/afternoon/evening/alts)
 * are stored separately in StepEntity; this holds everything else so the full itinerary
 * is DB-backed (not read from the static ItineraryData object).
 */
data class DayMeta(
    val date: String,
    val day: String,
    val title: String,
    val location: String,
    val steps: Int,
    val food: PriorityObjective? = null,
    val snack: PriorityObjective? = null,
    val alts: List<ContingencyPlan> = emptyList(),
    val markers: List<MapMarker> = emptyList()
)

fun ItineraryDay.toMeta(): DayMeta =
    DayMeta(date, day, title, location, steps, food, snack, alts, markers)

object DayMetaJson {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(DayMeta::class.java)
    fun toJson(meta: DayMeta): String = adapter.toJson(meta)
    fun fromJson(json: String): DayMeta? = try { adapter.fromJson(json) } catch (e: Exception) { null }
}
