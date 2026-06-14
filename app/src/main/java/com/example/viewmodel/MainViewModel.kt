package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: JapanMissionRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = JapanMissionRepository(database)
        // First launch: seed the persisted steps from the static itinerary.
        viewModelScope.launch { repository.seedStepsIfEmpty(ItineraryData.days) }
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

    // Itinerary days = static day metadata (title/location/food/markers…) combined with
    // the editable steps loaded from the database.
    val itineraryDays: StateFlow<List<ItineraryDay>> = allSteps
        .map { steps -> buildDays(steps) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItineraryData.days)

    private fun buildDays(steps: List<StepEntity>): List<ItineraryDay> {
        val byDay = steps.groupBy { it.dayIndex }
        return ItineraryData.days.mapIndexed { dayIndex, day ->
            val daySteps = byDay[dayIndex] ?: emptyList()
            fun period(p: String) = daySteps.filter { it.period == p }.sortedBy { it.time }.map { it.toStep() }
            day.copy(
                morning = period("morning"),
                afternoon = period("afternoon"),
                evening = period("evening"),
                customAlts = period("alternative")
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
        val allSteps = days.flatMap { it.morning + it.afternoon + it.evening + it.customAlts }
        val total = allSteps.size
        val completed = allSteps.count { checks[it.id] == true }
        if (total == 0) 0 else ((completed.toFloat() / total) * 100).toInt()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

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
