package com.loancaculator.ui.screen.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loancaculator.data.db.CalculationHistoryEntity
import com.loancaculator.data.finance.CalculationRepository
import com.loancaculator.data.finance.CalculatorType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FinanceViewModel @Inject constructor(private val repository: CalculationRepository) : ViewModel() {
    val history = repository.history().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val compare = repository.compare().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val clocks = repository.clocks().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save(type: CalculatorType, input: String, summary: String, onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.save(CalculationHistoryEntity(
                calculatorType = type.key,
                title = type.label,
                category = type.category,
                createdAt = System.currentTimeMillis(),
                inputJson = input,
                resultSummary = summary,
            ))
            onSaved(id)
        }
    }

    fun addCompare(id: Long) { viewModelScope.launch { repository.addCompare(id) } }
    fun removeCompare(item: com.loancaculator.data.db.CompareItemEntity) { viewModelScope.launch { repository.removeCompare(item) } }
    fun addClock(city: String, zoneId: String) { viewModelScope.launch { repository.addClock(city, zoneId) } }
    fun removeClock(item: com.loancaculator.data.db.WorldClockEntryEntity) { viewModelScope.launch { repository.removeClock(item) } }
    fun clearHistory() { viewModelScope.launch { repository.clearHistory() } }
    fun deleteHistory(id: Long) { viewModelScope.launch { repository.delete(id) } }
}
