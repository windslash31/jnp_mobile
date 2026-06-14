package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: JapanMissionRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = JapanMissionRepository(database)
    }

    // Active screen tab state
    private val _activeTab = MutableStateFlow("plan")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    // Selected Day Index state
    private val _selectedDayIndex = MutableStateFlow(0)
    val selectedDayIndex: StateFlow<Int> = _selectedDayIndex.asStateFlow()

    // Itinerary List State (In-Memory Editable)
    private val _itineraryDays = MutableStateFlow(ItineraryData.days)
    val itineraryDays: StateFlow<List<ItineraryDay>> = _itineraryDays.asStateFlow()

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

    // Auto-computed checklist progress
    val progressPercent: StateFlow<Int> = combine(itineraryChecks, itineraryDays) { checks, days ->
        var total = 0
        var completed = 0
        days.forEachIndexed { dIdx, day ->
            day.morning.forEachIndexed { iIdx, _ ->
                total++
                if (checks["d$dIdx-m-$iIdx"] == true) completed++
            }
            day.afternoon.forEachIndexed { iIdx, _ ->
                total++
                if (checks["d$dIdx-a-$iIdx"] == true) completed++
            }
            day.evening.forEachIndexed { iIdx, _ ->
                total++
                if (checks["d$dIdx-e-$iIdx"] == true) completed++
            }
        }
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

    // --- Editor Actions ---
    data class DeletedItem(val dayIndex: Int, val period: String, val stepIndex: Int, val step: ItineraryStep)
    private var lastDeletedItem: DeletedItem? = null

    fun removeStep(dayIndex: Int, period: String, stepIndex: Int) {
        val days = _itineraryDays.value.toMutableList()
        val currentDay = days[dayIndex]
        
        val deletedStep = when (period) {
            "morning" -> currentDay.morning[stepIndex]
            "afternoon" -> currentDay.afternoon[stepIndex]
            "evening" -> currentDay.evening[stepIndex]
            else -> return
        }
        lastDeletedItem = DeletedItem(dayIndex, period, stepIndex, deletedStep)

        val newDay = when (period) {
            "morning" -> currentDay.copy(morning = currentDay.morning.toMutableList().apply { removeAt(stepIndex) })
            "afternoon" -> currentDay.copy(afternoon = currentDay.afternoon.toMutableList().apply { removeAt(stepIndex) })
            "evening" -> currentDay.copy(evening = currentDay.evening.toMutableList().apply { removeAt(stepIndex) })
            "alternative" -> currentDay.copy(customAlts = currentDay.customAlts.toMutableList().apply { removeAt(stepIndex) })
            else -> currentDay
        }
        days[dayIndex] = newDay
        _itineraryDays.value = days
    }

    fun undoLastDelete() {
        val last = lastDeletedItem ?: return
        val days = _itineraryDays.value.toMutableList()
        if (last.dayIndex >= days.size) return
        val currentDay = days[last.dayIndex]
        val newDay = when (last.period) {
            "morning" -> currentDay.copy(morning = currentDay.morning.toMutableList().apply { add(last.stepIndex.coerceIn(0, size), last.step) })
            "afternoon" -> currentDay.copy(afternoon = currentDay.afternoon.toMutableList().apply { add(last.stepIndex.coerceIn(0, size), last.step) })
            "evening" -> currentDay.copy(evening = currentDay.evening.toMutableList().apply { add(last.stepIndex.coerceIn(0, size), last.step) })
            else -> currentDay
        }
        days[last.dayIndex] = newDay
        _itineraryDays.value = days
        lastDeletedItem = null
    }

    fun updateStep(dayIndex: Int, period: String, stepIndex: Int, step: ItineraryStep) {
        val days = _itineraryDays.value.toMutableList()
        val currentDay = days[dayIndex]
        val newDay = when (period) {
            "morning" -> currentDay.copy(morning = currentDay.morning.toMutableList().apply { set(stepIndex, step) }.sortedBy { it.time })
            "afternoon" -> currentDay.copy(afternoon = currentDay.afternoon.toMutableList().apply { set(stepIndex, step) }.sortedBy { it.time })
            "evening" -> currentDay.copy(evening = currentDay.evening.toMutableList().apply { set(stepIndex, step) }.sortedBy { it.time })
            "alternative" -> currentDay.copy(customAlts = currentDay.customAlts.toMutableList().apply { set(stepIndex, step) }.sortedBy { it.time })
            else -> currentDay
        }
        days[dayIndex] = newDay
        _itineraryDays.value = days
    }

    fun addStep(dayIndex: Int, period: String, step: ItineraryStep) {
        val days = _itineraryDays.value.toMutableList()
        val currentDay = days[dayIndex]
        val newDay = when (period) {
            "morning" -> currentDay.copy(morning = currentDay.morning.toMutableList().apply { add(step) }.sortedBy { it.time })
            "afternoon" -> currentDay.copy(afternoon = currentDay.afternoon.toMutableList().apply { add(step) }.sortedBy { it.time })
            "evening" -> currentDay.copy(evening = currentDay.evening.toMutableList().apply { add(step) }.sortedBy { it.time })
            "alternative" -> currentDay.copy(customAlts = currentDay.customAlts.toMutableList().apply { add(step) }.sortedBy { it.time })
            else -> currentDay
        }
        days[dayIndex] = newDay
        _itineraryDays.value = days
    }
}
