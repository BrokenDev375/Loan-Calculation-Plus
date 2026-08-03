package com.loancaculator.ui.screen.finance

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.loancaculator.R
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val supportedCurrencies = listOf("EUR", "USD", "GBP", "JPY", "VND")

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ApiConverterScreen(onBack: () -> Unit, viewModel: ExchangeRateViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var amount by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) { viewModel.loadBase("EUR") }

    val units = supportedCurrencies.map { code ->
        val currency = loanCurrency(code)
        ToolUnit(code, currencyName(code), currency.symbol, currency.flag)
    }

    val fromUnit = units.firstOrNull { it.id == state.base } ?: units.first()
    val toUnit = units.firstOrNull { it.id == state.target } ?: units[1]
    val rate = state.rates[state.target]
    val output = amount.toDoubleOrNull()?.let { value -> rate?.let { value * it } }
    
    val loadingRate = stringResource(R.string.loading_exchange_rate)
    val updatedText = state.lastUpdatedAt?.let {
        stringResource(R.string.updated, SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date(it)))
    }
    val ratesByApi = stringResource(R.string.rates_by_api)
    val footer = buildString {
        state.error?.let { append(it) }
        updatedText?.let {
            if (isNotEmpty()) append("\n")
            append(it)
        }
        if (isNotEmpty()) append("\n")
        append(ratesByApi)
    }

    ToolConverterLayout(
        title = stringResource(R.string.exchange_rate),
        onBack = onBack,
        fromValue = amount,
        onFromValueChange = { amount = it },
        toValue = when {
            rate == null && state.isLoading -> "..."
            output == null -> "0"
            else -> String.format(Locale.US, "%.4f", output)
        },
        fromUnit = fromUnit,
        toUnit = toUnit,
        units = units,
        fromNote = rate?.let { "1 ${state.base} \u2248 ${String.format(Locale.US, "%.4f", it)} ${state.target}" } ?: loadingRate,
        toNote = rate?.let { "1 ${state.target} \u2248 ${String.format(Locale.US, "%.3f", 1.0 / it)} ${state.base}" } ?: loadingRate,
        onFromUnitChange = { viewModel.loadBase(it) },
        onToUnitChange = viewModel::setTarget,
        onSwap = { if (!state.isLoading) viewModel.swap() },
        onReset = {
            amount = ""
            viewModel.reset()
        },
        onCalculate = { },
        calculateEnabled = !state.isLoading && rate != null,
        footer = footer,
    )
}
