package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val supportedCurrencies = loanCurrencies.map { it.code }

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ApiConverterScreen(onBack: () -> Unit, viewModel: ExchangeRateViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var amount by remember { mutableStateOf("1") }
    var result by remember { mutableStateOf<Double?>(null) }
    LaunchedEffect(Unit) { viewModel.loadBase("GBP") }

    val targetOptions = supportedCurrencies.filter { it != state.base && (state.rates.isEmpty() || state.rates.containsKey(it)) }
    Scaffold(topBar = { FinanceTopBar("Exchange Rate", "Live reference rates", onBack, compact = true, actions = { IconButton(onClick = viewModel::refresh) { Icon(Icons.Default.Refresh, "Refresh", tint = Color.White) } }) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Choose both currencies", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CurrencyMenu("Base", state.base, supportedCurrencies, Modifier.weight(1f)) { viewModel.loadBase(it) }
                CurrencyMenu("Target", state.target, targetOptions, Modifier.weight(1f)) { viewModel.setTarget(it); result = null }
            }
            OutlinedTextField(value = amount, onValueChange = { amount = it.filter { char -> char.isDigit() || char == '.' }; result = null }, label = { Text("Amount in ${state.base}") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            if (state.isLoading) Text("Loading current rates...", color = MaterialTheme.colorScheme.onSurfaceVariant)
            state.error?.let { Text(it, color = if (state.rates.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            Button(onClick = { result = amount.toDoubleOrNull()?.let { it * (state.rates[state.target] ?: 0.0) } }, enabled = !state.isLoading && state.rates[state.target] != null, modifier = Modifier.fillMaxWidth()) { Text("Convert") }
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F6F3))) {
                Column(Modifier.padding(18.dp)) {
                    Text("Result", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(result?.let { "%.2f %s".format(Locale.US, it, state.target) } ?: "-", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF0B2E4F))
                    state.rates[state.target]?.let { Text("1 ${state.base} = ${"%.6f".format(Locale.US, it)} ${state.target}", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
            state.lastUpdatedAt?.let { Text("Updated ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date(it))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Text("Rates by Exchange Rate API", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
private fun CurrencyMenu(label: String, selected: String, options: List<String>, modifier: Modifier, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text("${loanCurrency(selected).flag}  $selected"); Spacer(Modifier.weight(1f)); Text("v") }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { code ->
                    DropdownMenuItem(
                        leadingIcon = { Text(loanCurrency(code).flag) },
                        text = { Text(code) },
                        onClick = { expanded = false; onSelected(code) }
                    )
                }
            }
        }
    }
}
