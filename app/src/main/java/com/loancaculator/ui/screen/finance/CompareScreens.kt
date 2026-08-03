package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loancaculator.R
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.data.db.CalculationHistoryEntity
import com.loancaculator.data.db.CompareItemEntity
import com.loancaculator.data.finance.CalculatorType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CompareScreen(onNavigate: (String) -> Unit, onOpen: (CalculatorType) -> Unit) {
    val loanTypes = listOf(CalculatorType.PERSONAL, CalculatorType.BUSINESS, CalculatorType.MORTGAGE, CalculatorType.AUTO)
    Scaffold(
        topBar = { FinanceTopBar(stringResource(R.string.nav_compare), compact = true) },
        bottomBar = { FinanceBottomBar("compare", onNavigate) },
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 18.dp),
        ) {
            item {
                NativeAdSlot(
                    "native_compare",
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(100.dp),
                    isSmall = true,
                )
            }
            loanTypes.forEach { type ->
                item { CompareLoanCard(type) { onOpen(type) } }
            }
        }
    }
}

@Composable
private fun CompareLoanCard(type: CalculatorType, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().heightIn(min = 74.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
            FinanceLoanIcon(compareIconIndex(type), Modifier.size(42.dp))
            Column(Modifier.weight(1f).padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(compareTitle(type), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color(0xFF10233A))
                Text(compareDescription(type), style = MaterialTheme.typography.bodySmall, color = Color(0xFF5B7186))
            }
            CompareArrow()
        }
    }
}

@Composable
fun CompareDetailScreen(
    type: CalculatorType,
    onBack: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel(),
) {
    val history by viewModel.history.collectAsState()
    val compareItems by viewModel.compare.collectAsState()
    val historyById = remember(history) { history.associateBy { it.id } }
    val comparisons = compareItems.mapNotNull { compareItem ->
        historyById[compareItem.historyId]
            ?.takeIf { it.calculatorType == type.key && it.category == "Loans" }
            ?.let { CompareHistoryEntry(compareItem, it) }
    }
    val carouselState = rememberLazyListState()
    val selectedIndex = carouselState.firstVisibleItemIndex.coerceIn(0, (comparisons.size - 1).coerceAtLeast(0))
    val cardWidth = LocalConfiguration.current.screenWidthDp.dp * 0.9f

    Scaffold(
        containerColor = Color(0xFFD0EFFF),
        topBar = { FinanceTopBar(compareTitle(type), onBack = onBack, compact = true) },
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 30.dp),
        ) {
            item {
                NativeAdSlot(
                    "native_compare_detail",
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(240.dp),
                    isSmall = false,
                )
            }
            if (comparisons.isEmpty()) {
                item { EmptyCompareState() }
            } else {
                item {
                    LazyRow(
                        state = carouselState,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        itemsIndexed(comparisons, key = { _, entry -> entry.compareItem.id }) { index, entry ->
                            CompareResultCard(
                                entry = entry,
                                index = index,
                                cardWidth = cardWidth,
                                onRemove = { viewModel.removeCompare(entry.compareItem) },
                            )
                        }
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        comparisons.forEachIndexed { index, _ ->
                            Box(
                                Modifier
                                    .size(height = 8.dp, width = if (index == selectedIndex) 16.dp else 8.dp)
                                    .background(if (index == selectedIndex) Color(0xFF00A6CE) else Color(0xFFC4DEE5), CircleShape),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCompareState() {
    Column(
        modifier = Modifier.fillMaxWidth().height(460.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.compare_empty_state),
            contentDescription = null,
            modifier = Modifier.size(250.dp),
        )
        Text(stringResource(R.string.no_data), color = Color(0xFF10233A), style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
private fun CompareResultCard(
    entry: CompareHistoryEntry,
    index: Int,
    cardWidth: androidx.compose.ui.unit.Dp,
    onRemove: () -> Unit,
) {
    val type = CalculatorType.fromKey(entry.history.calculatorType)
    val info = compareInfoEntries(entry.history, type)
    val result = compareResultEntries(entry.history, type)
    Card(
        modifier = Modifier.width(cardWidth),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column {
            Row(
                Modifier.fillMaxWidth().background(Color(0xFF00A6CE)).padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                IconButton(onClick = onRemove, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.remove_comparison), tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
            Column(Modifier.padding(horizontal = 20.dp, vertical = 18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                info.forEach { (label, value) -> CompareValueRow(label, value) }
                Divider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEBF3F6))
                result.forEach { (label, value) -> CompareValueRow(label, value, emphasize = true) }
            }
        }
    }
}

@Composable
private fun CompareValueRow(label: String, value: String, emphasize: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(compareLabel(label), Modifier.weight(0.52f).padding(end = 12.dp), color = Color(0xFF5B7186), style = MaterialTheme.typography.bodyMedium)
        Text(localizedCompareValue(value), Modifier.weight(0.48f), color = Color(0xFF10233A), fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal, style = if (emphasize) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
    }
}

@Composable
private fun localizedCompareValue(value: String): String {
    val duration = Regex("^(\\d+) (Year|Years|Month|Months)$").matchEntire(value.trim())
    return when {
        value == "Year" -> stringResource(R.string.year)
        value == "Month" -> stringResource(R.string.month)
        duration?.groupValues?.get(2) == "Year" || duration?.groupValues?.get(2) == "Years" ->
            stringResource(R.string.duration_years, duration.groupValues[1].toInt())
        duration?.groupValues?.get(2) == "Month" || duration?.groupValues?.get(2) == "Months" ->
            stringResource(R.string.duration_months, duration.groupValues[1].toInt())
        else -> value
    }
}

@Composable
private fun CompareArrow() {
    Box(Modifier.size(26.dp).background(Color(0xFFE1EFF5), CircleShape), contentAlignment = Alignment.Center) {
        FinanceArrow()
    }
}

private data class CompareHistoryEntry(val compareItem: CompareItemEntity, val history: CalculationHistoryEntity)

private fun compareInfoEntries(item: CalculationHistoryEntity, type: CalculatorType): List<Pair<String, String>> {
    val currencyCode = loanCurrencyCode(item.inputJson)
    val result = mutableListOf<Pair<String, String>>()
    fun moneyValue(label: String, key: String) {
        inputValue(item.inputJson, key)?.toDoubleOrNull()?.let { result += label to money(it, currencyCode) }
    }
    fun rateValue() {
        val raw = inputValue(item.inputJson, "rate")
            ?: summaryEntries(item.resultSummary).firstOrNull { it.first.equals("Interest Rate", ignoreCase = true) }?.second
        raw?.toDoubleOrNull()?.let { result += "Interest Rate" to "${String.format(Locale.US, "%.2f", it)}%" }
    }
    fun termValue(label: String = "Loan Term") {
        inputValue(item.inputJson, "months")?.toIntOrNull()?.let { result += label to compareTerm(it) }
    }
    if (type == CalculatorType.MORTGAGE) {
        moneyValue("Home Price", "homePrice")
        rateValue()
        termValue()
        moneyValue("Down Payment", "downPayment")
        moneyValue("Property Tax", "propertyTax")
        moneyValue("PMI", "pmi")
        moneyValue("Home insurance", "homeInsurance")
        moneyValue("HOA Fees", "hoaFees")
    } else {
        moneyValue("Loan Amount", "principal")
        rateValue()
        termValue()
        if (type == CalculatorType.BUSINESS) {
            val frequency = inputValue(item.inputJson, "frequency")
                ?: inputValue(item.inputJson, "months")?.toIntOrNull()?.let { if (it % 12 == 0) "Year" else "Month" }
                ?: "Month"
            result += "Payment Frequency" to frequency
        }
        inputValue(item.inputJson, "startDate")?.toLongOrNull()?.let {
            result += "Start Date" to SimpleDateFormat("M/d/yy", Locale.US).format(Date(it))
        }
    }
    return result
}

private fun compareResultEntries(item: CalculationHistoryEntity, type: CalculatorType): List<Pair<String, String>> {
    val currencyCode = loanCurrencyCode(item.inputJson)
    val summary = summaryEntries(item.resultSummary)
    fun find(label: String): String? = summary.firstOrNull { it.first.equals(label, ignoreCase = true) }?.second
    fun moneyResult(label: String, source: String): Pair<String, String>? = find(source)?.toDoubleOrNull()?.let { label to money(it, currencyCode) }
    val result = mutableListOf<Pair<String, String>>()
    when (type) {
        CalculatorType.BUSINESS -> moneyResult("Monthly Payment", "Monthly payment")?.let(result::add)
        CalculatorType.MORTGAGE -> {
            moneyResult("Monthly Payment", "Monthly payment")?.let(result::add)
            moneyResult("Total Payment", "Total payment")?.let(result::add)
        }
        else -> {
            moneyResult("Monthly Payment", "Monthly payment")?.let(result::add)
            moneyResult("Total Payment", "Total payment")?.let(result::add)
            moneyResult("Total Interest Paid", "Total interest")?.let(result::add)
            val start = inputValue(item.inputJson, "startDate")?.toLongOrNull()
            val payoffMonths = find("Payoff months")?.toDoubleOrNull()?.toInt()
            if (start != null && payoffMonths != null) {
                result += "Pay-Off Date" to comparePayoffDate(start, payoffMonths)
            }
        }
    }
    return result
}

private fun comparePayoffDate(startMillis: Long, months: Int): String = SimpleDateFormat("M/d/yy", Locale.US).format(
    Calendar.getInstance().apply {
        timeInMillis = startMillis
        add(Calendar.MONTH, months)
    }.time,
)

private fun compareTerm(months: Int): String = if (months % 12 == 0) {
    val years = months / 12
    "$years Year${if (years == 1) "" else "s"}"
} else {
    "$months Month${if (months == 1) "" else "s"}"
}

@Composable
private fun compareTitle(type: CalculatorType): String = stringResource(
    when (type) {
        CalculatorType.PERSONAL -> R.string.compare_personal
        CalculatorType.BUSINESS -> R.string.compare_business
        CalculatorType.MORTGAGE -> R.string.compare_mortgage
        CalculatorType.AUTO -> R.string.compare_auto
        else -> R.string.nav_compare
    },
)

private fun compareIconIndex(type: CalculatorType): Int = when (type) {
    CalculatorType.PERSONAL -> 0
    CalculatorType.BUSINESS -> 1
    CalculatorType.MORTGAGE -> 2
    CalculatorType.AUTO -> 3
    else -> 0
}

@Composable
private fun compareDescription(type: CalculatorType): String = stringResource(
    when (type) {
        CalculatorType.PERSONAL -> R.string.compare_personal_desc
        CalculatorType.BUSINESS -> R.string.compare_business_desc
        CalculatorType.MORTGAGE -> R.string.compare_mortgage_desc
        CalculatorType.AUTO -> R.string.compare_auto_desc
        else -> R.string.no_saved_calculations
    },
)

@Composable
private fun compareLabel(label: String): String = when (label.lowercase(Locale.US)) {
    "home price" -> stringResource(R.string.home_price)
    "loan amount" -> stringResource(R.string.loan_amount)
    "interest rate" -> stringResource(R.string.interest_rate)
    "loan term" -> stringResource(R.string.loan_term)
    "down payment" -> stringResource(R.string.down_payment)
    "property tax" -> stringResource(R.string.property_tax)
    "pmi" -> stringResource(R.string.pmi)
    "home insurance" -> stringResource(R.string.home_insurance)
    "hoa fees" -> stringResource(R.string.hoa_fees)
    "payment frequency" -> stringResource(R.string.payment_frequency)
    "start date" -> stringResource(R.string.start_date)
    "monthly payment" -> stringResource(R.string.monthly_payment)
    "total payment" -> stringResource(R.string.total_payment)
    "total interest paid" -> stringResource(R.string.total_interest_paid)
    "pay-off date" -> stringResource(R.string.payoff_date)
    else -> label
}
