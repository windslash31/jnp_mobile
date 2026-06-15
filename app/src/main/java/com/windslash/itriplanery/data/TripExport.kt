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

data class ObjectiveExport(
    val name: String,
    val type: String = "",
    val budget: Int = 0,
    val query: String = "",
    val time: String? = null
)

data class ContingencyExport(
    val name: String,
    val budget: Int = 0,
    val desc: String = "",
    val query: String = "",
    val match: String? = null
)

data class MarkerExport(
    val lat: Double,
    val lng: Double,
    val type: String = "visit",
    val seq: Int = 0,
    val title: String = "",
    val query: String = "",
    val meta: String? = null
)

data class DayExport(
    val date: String,
    val day: String = "",
    val title: String = "",
    val location: String = "",
    val steps: Int = 0,
    val morning: List<StepExport> = emptyList(),
    val afternoon: List<StepExport> = emptyList(),
    val evening: List<StepExport> = emptyList(),
    val alternatives: List<StepExport> = emptyList(),
    val food: ObjectiveExport? = null,
    val snack: ObjectiveExport? = null,
    val contingencies: List<ContingencyExport> = emptyList(),
    val markers: List<MarkerExport> = emptyList()
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
    fun PriorityObjective.toExport() = ObjectiveExport(name, type, budget, query, time)
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
        days = days.map { d ->
            DayExport(
                date = d.date,
                day = d.day,
                title = d.title,
                location = d.location,
                steps = d.steps,
                morning = d.morning.toExport(),
                afternoon = d.afternoon.toExport(),
                evening = d.evening.toExport(),
                alternatives = d.customAlts.toExport(),
                food = d.food?.toExport(),
                snack = d.snack?.toExport(),
                contingencies = d.alts.map { ContingencyExport(it.name, it.budget, it.desc, it.query, it.match) },
                markers = d.markers.map { MarkerExport(it.lat, it.lng, it.type, it.seq, it.title, it.query, it.meta) }
            )
        }
    )
}
