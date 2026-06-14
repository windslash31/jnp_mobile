package com.windslash.itriplanery.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.windslash.itriplanery.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: JapanMissionRepository
    private val settings = SettingsRepository(application)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = JapanMissionRepository(database)
        // First launch: seed the persisted steps from the static itinerary and a default trip.
        viewModelScope.launch {
            repository.seedTripIfEmpty()
            repository.seedDaysIfEmpty(ItineraryData.days)
            repository.seedStepsIfEmpty(ItineraryData.days)
        }
    }

    // The active trip drives destination/budget/currency/traveler names (replaces the
    // hardcoded UI values). Null until seeding completes on first launch.
    val activeTrip: StateFlow<TripEntity?> = repository.activeTrip
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateTrip(trip: TripEntity) {
        viewModelScope.launch { repository.updateTrip(trip) }
    }

    /** Serialize the active trip + its itinerary to portable JSON (for export/share). */
    fun exportJson(): String? {
        val trip = activeTrip.value ?: return null
        return TripJson.toJson(buildTripExport(trip, itineraryDays.value))
    }

    /**
     * Import a trip from portable JSON: overwrites the active trip's metadata and replaces
     * its entire itinerary (days + steps). Returns false if the JSON is invalid / no active trip.
     */
    fun importJson(json: String): Boolean {
        val env = TripJson.fromJson(json) ?: return false
        val current = activeTrip.value ?: return false
        viewModelScope.launch {
            repository.updateTrip(
                current.copy(
                    name = env.trip.name,
                    destination = env.trip.destination,
                    startDate = env.trip.startDate,
                    endDate = env.trip.endDate,
                    currencyCode = env.trip.currencyCode,
                    budgetAmount = env.trip.budgetAmount,
                    travelerNames = env.trip.travelerNames
                )
            )
            val metas = env.days.mapIndexed { i, d ->
                DayMeta(date = d.date, day = "Day ${i + 1}", title = d.title, location = d.location, steps = 0)
            }
            val stepsByDay = env.days.mapIndexed { i, d ->
                fun List<StepExport>.toEntities(period: String) = map {
                    StepEntity(
                        id = UUID.randomUUID().toString(), dayIndex = i, period = period,
                        time = it.time, text = it.text, meta = it.meta, cost = it.cost,
                        type = it.type, details = it.details, mapQuery = it.mapQuery
                    )
                }
                d.morning.toEntities("morning") + d.afternoon.toEntities("afternoon") + d.evening.toEntities("evening")
            }
            repository.replaceItinerary(metas, stepsByDay)
        }
        return true
    }

    // --- User preferences (persisted via DataStore) ---
    val darkMode: StateFlow<Boolean> = settings.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val gamificationEnabled: StateFlow<Boolean> = settings.gamificationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { settings.setDarkMode(enabled) }
    }

    fun setGamificationEnabled(enabled: Boolean) {
        viewModelScope.launch { settings.setGamificationEnabled(enabled) }
    }

    // Active screen tab state
    private val _activeTab = MutableStateFlow("plan")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    // Selected Day Index state
    private val _selectedDayIndex = MutableStateFlow(0)
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex.asStateFlow()

    // Persisted itinerary steps (single source of truth). Eager so edit actions can
    // read the current list synchronously to map a UI index -> the step's stable id.
    private val allSteps: StateFlow<List<StepEntity>> = repository.allSteps
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Day metadata, now persisted in the DB (not the static ItineraryData object).
    private val allDays: StateFlow<List<DayMeta>> = repository.allDays
        .stateIn(viewModelScope, SharingStarted.Eagerly, ItineraryData.days.map { it.toMeta() })

    // Itinerary days = persisted day metadata combined with the editable steps from the DB.
    val itineraryDays: StateFlow<List<ItineraryDay>> =
        combine(allSteps, allDays) { steps, metas -> buildDays(steps, metas) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItineraryData.days)

    private fun buildDays(steps: List<StepEntity>, metas: List<DayMeta>): List<ItineraryDay> {
        // Fall back to the static metadata on the very first frame before seeding emits.
        val dayMetas = if (metas.isEmpty()) ItineraryData.days.map { it.toMeta() } else metas
        val byDay = steps.groupBy { it.dayIndex }
        return dayMetas.mapIndexed { dayIndex, meta ->
            val daySteps = byDay[dayIndex] ?: emptyList()
            fun period(p: String) = daySteps.filter { it.period == p }.sortedBy { it.time }.map { it.toStep() }
            ItineraryDay(
                date = meta.date, day = meta.day, title = meta.title, location = meta.location, steps = meta.steps,
                morning = period("morning"), afternoon = period("afternoon"), evening = period("evening"),
                food = meta.food, snack = meta.snack, alts = meta.alts,
                customAlts = period("alternative"), markers = meta.markers
            )
        }
    }

    // Current persisted steps for a day/period, ordered the same way buildDays orders them,
    // so a UI step index maps to the right entity.
    private fun stepsFor(dayIndex: Int, period: String): List<StepEntity> =
        allSteps.value.filter { it.dayIndex == dayIndex && it.period == period }.sortedBy { it.time }

    // Transactions Flow
    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Itinerary Checks Flow
    val itineraryChecks: StateFlow<Map<String, Boolean>> = repository.allItineraryChecks
        .map { list -> list.associate { it.key to it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Food Checks Flow
    val foodChecks: StateFlow<Map<String, Boolean>> = repository.allFoodChecks
        .map { list -> list.associate { it.itemId to it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Auto-computed checklist progress. Counts every editable step (incl. Alternatives)
    // keyed by its stable id, so it stays correct when steps are added/reordered.
    val progressPercent: StateFlow<Int> = combine(itineraryChecks, itineraryDays) { checks, days ->
        itineraryProgress(days, checks)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Packing list
    val packingItems: StateFlow<List<PackingItemEntity>> = repository.allPacking
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addPackingItem(label: String) {
        if (label.isBlank()) return
        viewModelScope.launch { repository.addPackingItem(label.trim()) }
    }

    fun togglePacking(item: PackingItemEntity) {
        viewModelScope.launch { repository.togglePacking(item) }
    }

    fun deletePacking(id: Long) {
        viewModelScope.launch { repository.deletePacking(id) }
    }

    fun changeTab(tab: String) {
        _activeTab.value = tab
    }

    fun selectDay(index: Int) {
        _selectedDayIndex.value = index
    }

    fun addTransaction(desc: String, amount: Double, category: String, date: String, time: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    desc = desc,
                    amount = amount,
                    category = category,
                    date = date,
                    time = time
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun toggleItineraryCheck(key: String, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleItineraryCheck(key, completed)
        }
    }

    fun toggleFoodCheck(itemId: String, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleFoodCheck(itemId, completed)
        }
    }

    // --- Editor Actions (all persisted to Room) ---
    // Keeps the last deleted entity (with its stable id) so undo can re-insert it exactly.
    private var lastDeletedStep: StepEntity? = null

    fun removeStep(dayIndex: Int, period: String, stepIndex: Int) {
        val entity = stepsFor(dayIndex, period).getOrNull(stepIndex) ?: return
        lastDeletedStep = entity
        viewModelScope.launch { repository.deleteStep(entity.id) }
    }

    fun undoLastDelete() {
        val entity = lastDeletedStep ?: return
        lastDeletedStep = null
        viewModelScope.launch { repository.upsertStep(entity) }
    }

    fun updateStep(dayIndex: Int, period: String, stepIndex: Int, step: ItineraryStep) {
        // Reuse the existing step's id (so it's an update, not a duplicate insert).
        val existingId = stepsFor(dayIndex, period).getOrNull(stepIndex)?.id ?: UUID.randomUUID().toString()
        viewModelScope.launch { repository.upsertStep(step.toEntity(dayIndex, period, existingId)) }
    }

    fun addStep(dayIndex: Int, period: String, step: ItineraryStep) {
        viewModelScope.launch { repository.upsertStep(step.toEntity(dayIndex, period)) }
    }
}
