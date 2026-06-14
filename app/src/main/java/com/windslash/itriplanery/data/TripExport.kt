package com.windslash.itriplanery.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// --- Portable trip schema (shared by export #15 and import #25) ---
// Versioned so the format can evolve. Plain data classes serialized via Moshi reflection.

data class StepExport(
    val time: String,
    val text: String,
    val meta: String = "",
    val cost: Int = 0,
    val type: String = "other",
    val details: String? = null,
    val mapQuery: String? = null
)

data class DayExport(
    val date: String,
    val title: String = "",
    val location: String = "",
    val morning: List<StepExport> = emptyList(),
    val afternoon: List<StepExport> = emptyList(),
    val evening: List<StepExport> = emptyList()
)

data class TripExport(
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val currencyCode: String,
    val budgetAmount: Double,
    val travelerNames: String
)

data class TripExportEnvelope(
    val schemaVersion: Int = 1,
    val trip: TripExport,
    val days: List<DayExport> = emptyList()
)

object TripJson {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val adapter = moshi.adapter(TripExportEnvelope::class.java).indent("  ")

    fun toJson(envelope: TripExportEnvelope): String = adapter.toJson(envelope)

    /** Lenient parse; returns null on malformed input. */
    fun fromJson(json: String): TripExportEnvelope? = try {
        adapter.fromJson(json.trim())
    } catch (e: Exception) {
        null
    }
}

/** Build a portable envelope from the active trip + its (DB-backed) itinerary days. */
fun buildTripExport(trip: TripEntity, days: List<ItineraryDay>): TripExportEnvelope {
    fun List<ItineraryStep>.toExport() =
        map { StepExport(it.time, it.text, it.meta, it.cost, it.type, it.details, it.mapQuery) }
    return TripExportEnvelope(
        trip = TripExport(
            name = trip.name,
            destination = trip.destination,
            startDate = trip.startDate,
            endDate = trip.endDate,
            currencyCode = trip.currencyCode,
            budgetAmount = trip.budgetAmount,
            travelerNames = trip.travelerNames
        ),
        days = days.map {
            DayExport(
                date = it.date,
                title = it.title,
                location = it.location,
                morning = it.morning.toExport(),
                afternoon = it.afternoon.toExport(),
                evening = it.evening.toExport()
            )
        }
    )
}
