package com.loancaculator.ui.screen.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loancaculator.data.finance.ExchangeRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExchangeRateUiState(
    val base: String = "EUR",
    val target: String = "USD",
    val rates: Map<String, Double> = emptyMap(),
    val lastUpdatedAt: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(private val repository: ExchangeRateRepository) : ViewModel() {
    private val _state = MutableStateFlow(ExchangeRateUiState())
    val state: StateFlow<ExchangeRateUiState> = _state.asStateFlow()

    fun loadBase(base: String, forceRefresh: Boolean = false) {
        val normalized = base.uppercase()
        if (_state.value.isLoading || (!forceRefresh && _state.value.base == normalized && _state.value.rates.isNotEmpty())) return
        _state.update {
            val target = if (it.target == normalized) if (normalized == "GBP") "USD" else "GBP" else it.target
            it.copy(base = normalized, target = target, rates = if (it.base == normalized) it.rates else emptyMap(), lastUpdatedAt = if (it.base == normalized) it.lastUpdatedAt else null, isLoading = true, error = null)
        }
        viewModelScope.launch {
            runCatching { repository.load(normalized, forceRefresh) }
                .onSuccess { snapshot ->
                    _state.update { it.copy(base = snapshot.base, rates = snapshot.rates, lastUpdatedAt = snapshot.lastUpdatedAt, isLoading = false, error = if (snapshot.fromCache) "Using saved rates. Pull refresh when online." else null) }
                }
                .onFailure { error -> _state.update { it.copy(isLoading = false, error = error.message ?: "Unable to load exchange rates") } }
        }
    }

    fun setTarget(target: String) { _state.update { it.copy(target = target.uppercase()) } }

    fun swap() {
        val current = _state.value
        loadBase(current.target)
        setTarget(current.base)
    }

    fun reset() {
        val current = _state.value
        if (current.base == "EUR") {
            _state.update { it.copy(target = "USD", error = null) }
            if (current.rates.isEmpty() && !current.isLoading) loadBase("EUR")
        } else {
            loadBase("EUR")
            setTarget("USD")
        }
    }

    fun refresh() { loadBase(_state.value.base, forceRefresh = true) }
}
