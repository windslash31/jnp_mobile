package com.windslash.itriplanery.data

/**
 * Percentage (0..100) of editable itinerary steps that are checked off.
 *
 * Counts morning/afternoon/evening AND alternative steps, keyed by each step's stable
 * id (so the result stays correct when steps are added, removed, or reordered).
 * Pure function — extracted from the ViewModel so it can be unit-tested directly.
 */
fun itineraryProgress(days: List<ItineraryDay>, checks: Map<String, Boolean>): Int {
    val allSteps = days.flatMap { it.morning + it.afternoon + it.evening + it.customAlts }
    if (allSteps.isEmpty()) return 0
    val completed = allSteps.count { checks[it.id] == true }
    return ((completed.toFloat() / allSteps.size) * 100).toInt()
}
