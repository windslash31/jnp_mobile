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

/**
 * Build the Gourmet/Food list from the itinerary's food-type steps, so the Food tab reflects
 * the active/imported trip instead of a static hardcoded list. Pure + unit-testable.
 */
fun deriveFoodCategories(days: List<ItineraryDay>): List<FoodCategory> {
    val foodTypes = setOf("food", "sweets", "street")
    val items = days.flatMap { d ->
        (d.morning + d.afternoon + d.evening + d.customAlts)
            .filter { it.type.lowercase() in foodTypes }
            .map { s ->
                FoodItem(
                    id = s.id,
                    name = s.text,
                    dish = s.meta.ifBlank { s.type.replaceFirstChar { c -> c.uppercase() } },
                    area = d.day,
                    note = s.details ?: "",
                    mustTry = false
                )
            }
    }
    return if (items.isEmpty()) emptyList()
    else listOf(
        FoodCategory(
            id = "itinerary", name = "From Your Itinerary", icon = "🍽️",
            bgColor = "#FFFBEB", textColor = "#D97706", tagline = "Eat Protocol", items = items
        )
    )
}
