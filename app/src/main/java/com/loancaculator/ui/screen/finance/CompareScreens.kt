package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.data.db.CalculationHistoryEntity
import com.loancaculator.data.finance.CalculatorType

@Composable
fun CompareScreen(onNavigate: (String) -> Unit, onOpen: (Long) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    var selectedType by remember { mutableStateOf<CalculatorType?>(null) }
    val loanTypes = listOf(CalculatorType.PERSONAL, CalculatorType.BUSINESS, CalculatorType.MORTGAGE, CalculatorType.AUTO)
    Scaffold(topBar = { FinanceTopBar("Compare", "Review saved options side by side") }, bottomBar = { FinanceBottomBar("compare", onNavigate) }) { padding ->
        LazyColumn(Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            item { NativeAdSlot("native_compare", modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), isSmall = true) }
            item { Text("Loan comparison", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 20.dp)) }
            item { Text("Choose a loan type to review your saved calculations.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp)) }
            loanTypes.forEach { type ->
                item {
                    CompareLoanCard(type, selectedType == type) { selectedType = type }
                }
            }
            selectedType?.let { type ->
                item {
                    val selectedHistory = history.filter { it.calculatorType == type.key && it.category == "Loans" }
                    SelectedLoanData(type, selectedHistory, onOpen)
                }
            }
        }
    }
}

@Composable
private fun CompareLoanCard(type: CalculatorType, selected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(132.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            FinanceLoanIcon(compareIconIndex(type), Modifier.size(82.dp))
            Column(Modifier.weight(1f).padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(type.label, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(compareDescription(type), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (selected) Text("Selected", style = MaterialTheme.typography.labelMedium, color = Color(0xFF16B2D7))
            }
            FinanceArrow()
        }
    }
}

@Composable
private fun SelectedLoanData(type: CalculatorType, history: List<CalculationHistoryEntity>, onOpen: (Long) -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (history.isEmpty()) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("No data", style = MaterialTheme.typography.titleLarge, color = Color(0xFF16B2D7))
                    Text("No saved history for ${type.label} yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Text("Saved ${type.label} data", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            history.forEach { item ->
                Card(onClick = { onOpen(item.id) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.titleMedium)
                            Text("${loanCurrencyCode(item.inputJson)}  ${item.resultSummary.substringBefore("|")}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        FinanceArrow()
                    }
                }
            }
        }
    }
}

private fun compareIconIndex(type: CalculatorType): Int = when (type) {
    CalculatorType.PERSONAL -> 0
    CalculatorType.BUSINESS -> 1
    CalculatorType.MORTGAGE -> 2
    CalculatorType.AUTO -> 3
    else -> 0
}

private fun compareDescription(type: CalculatorType): String = when (type) {
    CalculatorType.PERSONAL -> "Estimate monthly payments and total interest."
    CalculatorType.BUSINESS -> "Review potential business loan costs."
    CalculatorType.MORTGAGE -> "Explore affordability and repayment options."
    CalculatorType.AUTO -> "Calculate car payments and total costs."
    else -> "Review saved loan calculations."
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun CompareDetailScreen(id: Long, onBack: () -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    val item = history.firstOrNull { it.id == id }
    val currencyCode = loanCurrencyCode(item?.inputJson.orEmpty())
    Scaffold(topBar = { FinanceTopBar("Compare detail", item?.title, onBack) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Saved result", style = MaterialTheme.typography.titleLarge)
            summaryEntries(item?.resultSummary.orEmpty()).forEach { (label, value) ->
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Row(Modifier.padding(14.dp)) { Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant); Text(resultValue(label, value, currencyCode)) } }
            }
        }
    }
}
