package com.loancaculator.ui.screen.finance

import android.graphics.BitmapFactory
import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loancaculator.advertisement.AdManager
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.core.IapOpener
import com.loancaculator.R
import com.loancaculator.ui.components.findActivity
import com.loancaculator.ui.components.dismissKeyboardOnTap
import com.loancaculator.ui.theme.Gold
import com.loancaculator.ui.theme.Primary
import com.loancaculator.data.db.CalculationHistoryEntity
import com.loancaculator.data.finance.CalculatorType
import com.loancaculator.data.finance.DepositInput
import com.loancaculator.data.finance.FinancialCalculator
import com.loancaculator.data.finance.LoanInput
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun FinanceHomeScreen(onNavigate: (String) -> Unit, onOpen: (CalculatorType) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    val recentHistory = history.take(6)
    val context = LocalContext.current
    fun openCalculator(type: CalculatorType) {
        val activity = context.findActivity()
        if (activity != null) AdManager.showInter(activity, "inter_home") { onOpen(type) } else onOpen(type)
    }
    Scaffold(bottomBar = { FinanceBottomBar("home", onNavigate) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 10.dp)) {
            item { FinanceHero() }
            item { CalculatorGrid(listOf(CalculatorType.PERSONAL, CalculatorType.BUSINESS, CalculatorType.MORTGAGE, CalculatorType.AUTO), ::openCalculator) }
            item { HomeSectionTitle(stringResource(R.string.investment)) }
            item { NativeAdSlot("native_home", modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(240.dp), isSmall = false) }
            item { InvestmentRow(CalculatorType.FD, ::openCalculator) }
            item { InvestmentRow(CalculatorType.RD, ::openCalculator) }
            item {
                Button(
                    onClick = { IapOpener.open(context, "home") },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFF2B632), Color(0xFFFFD76A), Color(0xFFFFF0A6), Color(0xFFFFD76A), Color(0xFFF2B632))),
                            RoundedCornerShape(24.dp),
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF5B3A00)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(
                        stringResource(R.string.premium_cta),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                    )
                }
            }
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.history), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { onNavigate("history") }, contentPadding = PaddingValues(0.dp)) {
                        Text(stringResource(R.string.view_all), color = Color(0xFF16B2D7), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End, maxLines = 2)
                    }
                }
            }
            if (recentHistory.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_calculations),
                        Modifier.padding(horizontal = 20.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                recentHistory.forEach { historyItem ->
                    item {
                        FinanceHistoryCard(historyItem, onOpen = { onNavigate("result/${historyItem.id}") })
                    }
                }
            }
        }
    }
}

@Composable
internal fun FinanceHistoryCard(
    item: CalculationHistoryEntity,
    onOpen: () -> Unit,
    viewAllStyle: Boolean = false,
    selectionMode: Boolean = false,
    selected: Boolean = false,
    onToggleSelected: () -> Unit = {},
) {
    val type = CalculatorType.fromKey(item.calculatorType)
    val stats = financeHistoryStats(item, type)

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .noRippleClickable { if (selectionMode) onToggleSelected() else onOpen() },
        shape = RoundedCornerShape(if (viewAllStyle) 20.dp else 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (selected) BorderStroke(2.dp, Color(0xFF16B2D7)) else null,
    ) {
        Column(
            Modifier.padding(if (viewAllStyle) PaddingValues(horizontal = 20.dp, vertical = 18.dp) else PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(if (viewAllStyle) 14.dp else 12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (type.category == "Loans") {
                    FinanceLoanIcon(type.iconIndex(), Modifier.size(44.dp))
                } else {
                    DepositSpriteIcon(if (type == CalculatorType.FD) 0 else 1, Modifier.size(44.dp))
                }
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(
                        calculatorLabel(type),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        SimpleDateFormat("M/d/yy", Locale.US).format(Date(item.createdAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (selectionMode) {
                    Checkbox(checked = selected, onCheckedChange = { onToggleSelected() })
                } else {
                    Box(
                        Modifier.size(28.dp).background(Color(0xFFE1EFF5), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        FinanceArrow()
                    }
                }
            }
            Canvas(Modifier.fillMaxWidth().height(1.dp)) {
                drawLine(
                    color = Color(0xFFD7E2E6),
                    start = androidx.compose.ui.geometry.Offset.Zero,
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)),
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                stats.forEach { (label, value) ->
                    Column(Modifier.weight(1f)) {
                        Text(financeStatLabel(label), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(localizedHistoryValue(value), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
    }
}

internal fun financeHistoryStats(item: CalculationHistoryEntity, type: CalculatorType): List<Pair<String, String>> {
    val currencyCode = loanCurrencyCode(item.inputJson)
    val rateValue = inputValue(item.inputJson, "rate")?.toDoubleOrNull()
        ?: summaryEntries(item.resultSummary).firstOrNull { it.first == "Interest Rate" }?.second?.toDoubleOrNull()
    val rate = rateValue?.let { "${String.format(Locale.US, "%.2f", it)}%" } ?: "-"
    val months = inputValue(item.inputJson, "months")?.toIntOrNull()
    val durationLabel = if (type.category == "Deposits") "Tenure" else "Duration"
    val duration = months?.let(::historyDuration) ?: "-"
    val amountKey = when {
        type == CalculatorType.MORTGAGE -> "homePrice"
        type == CalculatorType.RD -> "monthlyContribution"
        else -> "principal"
    }
    val amount = inputValue(item.inputJson, amountKey)?.toDoubleOrNull()?.let { money(it, currencyCode) } ?: "-"
    val amountLabel = when {
        type == CalculatorType.RD -> "Monthly Deposit"
        type == CalculatorType.FD -> "Investment Amount"
        else -> "Loan Amount"
    }
    return listOf("Interest" to rate, durationLabel to duration, amountLabel to amount)
}

@Composable
private fun financeStatLabel(label: String): String = when (label) {
    "Interest" -> stringResource(R.string.interest)
    "Tenure" -> stringResource(R.string.tenure)
    "Duration" -> stringResource(R.string.duration)
    "Monthly Deposit" -> stringResource(R.string.monthly_deposit)
    "Investment Amount" -> stringResource(R.string.investment_amount)
    else -> stringResource(R.string.loan_amount)
}

@Composable
private fun localizedHistoryValue(value: String): String {
    val duration = Regex("^(\\d+) (Year|Years|Month|Months)$").matchEntire(value.trim())
    return when (duration?.groupValues?.get(2)) {
        "Year", "Years" -> stringResource(R.string.duration_years, duration.groupValues[1].toInt())
        "Month", "Months" -> stringResource(R.string.duration_months, duration.groupValues[1].toInt())
        else -> value
    }
}

private fun historyDuration(months: Int): String {
    return if (months % 12 == 0) {
        val years = months / 12
        "$years Year${if (years == 1) "" else "s"}"
    } else {
        "$months Month${if (months == 1) "" else "s"}"
    }
}

@Composable
private fun HomeSectionTitle(title: String) { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 20.dp)) }

@Composable
private fun CalculatorGrid(types: List<CalculatorType>, onClick: (CalculatorType) -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        types.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { type -> Box(Modifier.weight(1f)) { CalculatorCard(type) { onClick(type) } } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InvestmentRow(type: CalculatorType, onClick: (CalculatorType) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .heightIn(min = 78.dp)
            .noRippleClickable { onClick(type) },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DepositSpriteIcon(if (type == CalculatorType.FD) 0 else 1, Modifier.size(56.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(if (type == CalculatorType.FD) "FD" else "RD", fontWeight = FontWeight.Bold)
                Text(calculatorLabel(type), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            Box(
                modifier = Modifier.size(42.dp).background(Color(0xFFE1EFF5), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                FinanceArrow()
            }
        }
    }
}

@Composable
private fun CalculatorCard(type: CalculatorType, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().heightIn(min = 116.dp).noRippleClickable(onClick = onClick), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FinanceLoanIcon(type.iconIndex(), Modifier.size(58.dp))
                FinanceArrow()
            }
            Text(calculatorLabel(type), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun CalculatorType.iconIndex(): Int = when (this) {
    CalculatorType.PERSONAL -> 0
    CalculatorType.BUSINESS -> 1
    CalculatorType.MORTGAGE -> 2
    CalculatorType.AUTO -> 3
    CalculatorType.FD -> 2
    CalculatorType.RD -> 0
}

@Composable
fun FinanceLoanIcon(index: Int, modifier: Modifier = Modifier.size(74.dp)) {
    val context = LocalContext.current
    val sprite = remember {
        BitmapFactory.decodeResource(context.resources, com.loancaculator.R.drawable.finance_tiles).asImageBitmap()
    }
    val sourceSize = IntSize(sprite.width / 2, sprite.height / 2)
    val sourceOffset = IntOffset(
        x = if (index % 2 == 1) sourceSize.width else 0,
        y = if (index >= 2) sourceSize.height else 0
    )
    Box(modifier.clip(RoundedCornerShape(18.dp))) {
        Canvas(Modifier.fillMaxSize()) {
            drawImage(
                image = sprite,
                srcOffset = sourceOffset,
                srcSize = sourceSize,
                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
            )
        }
    }
}

@Composable
private fun DepositSpriteIcon(index: Int, modifier: Modifier = Modifier.size(54.dp)) {
    val context = LocalContext.current
    val sprite = remember {
        BitmapFactory.decodeResource(context.resources, com.loancaculator.R.drawable.finance_deposits).asImageBitmap()
    }
    val sourceSize = IntSize(sprite.width / 2, sprite.height)
    val sourceOffset = IntOffset(sourceSize.width * index.coerceIn(0, 1), 0)
    Box(modifier.clip(RoundedCornerShape(14.dp))) {
        Canvas(Modifier.fillMaxSize()) {
            drawImage(
                image = sprite,
                srcOffset = sourceOffset,
                srcSize = sourceSize,
                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(type: CalculatorType, onBack: () -> Unit, onSaved: (Long) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    var principal by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var months by remember { mutableStateOf("") }
    var downPayment by remember { mutableStateOf("") }
    var downPaymentPercent by remember { mutableStateOf("") }
    var propertyTax by remember { mutableStateOf("") }
    var pmi by remember { mutableStateOf("") }
    var hoaFees by remember { mutableStateOf("") }
    var homeInsurance by remember { mutableStateOf("") }
    var compounding by remember { mutableStateOf("1") }
    var error by remember { mutableStateOf<String?>(null) }
    var currencyCode by remember { mutableStateOf("GBP") }
    var currencyMenuOpen by remember { mutableStateOf(false) }
    var termUnit by remember { mutableStateOf(if (type.category == "Deposits" || type == CalculatorType.BUSINESS) "Month" else "Year") }
    var termMenuOpen by remember { mutableStateOf(false) }
    var startDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var datePickerOpen by remember { mutableStateOf(false) }
    val currency = loanCurrency(currencyCode)
    val isLoan = type.category == "Loans"
    val isDeposit = type.category == "Deposits"
    val amountLabel = when {
        type == CalculatorType.MORTGAGE -> stringResource(R.string.home_price)
        type == CalculatorType.RD -> stringResource(R.string.monthly_deposit)
        isLoan -> stringResource(R.string.loan_amount)
        else -> stringResource(R.string.investment_amount)
    }
    val invalidInputError = stringResource(R.string.valid_amount_rate_term)
    val downPaymentError = stringResource(R.string.down_payment_lower)
    val calculatorAdHeight = if (type == CalculatorType.MORTGAGE) 260.dp else 140.dp
    Scaffold(
        modifier = Modifier.dismissKeyboardOnTap(),
        topBar = { FinanceTopBar(calculatorLabel(type), stringResource(R.string.enter_details), onBack, compact = true) },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            NativeAdSlot(
                "native_calculator",
                modifier = Modifier.fillMaxWidth().height(calculatorAdHeight),
                isSmall = type != CalculatorType.MORTGAGE,
            )
            LazyColumn(
                Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    FieldLabel(amountLabel, modifier = Modifier.weight(1f).padding(end = 8.dp))
                    Box {
                        OutlinedButton(onClick = { currencyMenuOpen = true }, shape = RoundedCornerShape(24.dp), colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF10233A))) {
                            Text("${currency.flag}  ${currency.code}")
                            FinanceArrow()
                        }
                        DropdownMenu(expanded = currencyMenuOpen, onDismissRequest = { currencyMenuOpen = false }) {
                            loanCurrencies.forEach { option ->
                                DropdownMenuItem(
                                    leadingIcon = { Text(option.flag) },
                                    text = { Text("${option.symbol}  ${option.code} - ${currencyName(option.code)}") },
                                    onClick = { currencyCode = option.code; currencyMenuOpen = false }
                                )
                            }
                        }
                    }
                }
            }
            if (type == CalculatorType.MORTGAGE) {
                item {
                    FinanceField(stringResource(R.string.home_price), principal, {
                        principal = it
                        val home = it.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                        val currentDown = downPayment.toDoubleOrNull()?.coerceAtLeast(0.0)
                        val currentPercent = downPaymentPercent.toDoubleOrNull()?.coerceIn(0.0, 100.0)
                        if (it.isEmpty()) {
                            downPayment = ""
                            downPaymentPercent = ""
                        } else if (home > 0.0) {
                            when {
                                currentDown != null -> {
                                    val clampedDown = currentDown.coerceAtMost(home)
                                    downPayment = formatMortgageAmount(clampedDown)
                                    downPaymentPercent = formatMortgagePercent(clampedDown / home * 100.0)
                                }
                                currentPercent != null -> {
                                    downPaymentPercent = formatMortgagePercent(currentPercent)
                                    downPayment = formatMortgageAmount(home * currentPercent / 100.0)
                                }
                            }
                        }
                        }, prefix = "${currency.symbol} ")
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinanceField(stringResource(R.string.down_payment), downPayment, {
                            val home = principal.toDoubleOrNull() ?: 0.0
                            val requestedDown = it.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                            if (it.isEmpty()) {
                                downPayment = ""
                                downPaymentPercent = ""
                            } else if (home > 0.0) {
                                val clampedDown = requestedDown.coerceAtMost(home)
                                downPayment = if (requestedDown > home) formatMortgageAmount(clampedDown) else it
                                downPaymentPercent = formatMortgagePercent(clampedDown / home * 100.0)
                            } else {
                                downPayment = it
                                downPaymentPercent = ""
                            }
                        }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                        FinanceField(stringResource(R.string.down_payment), downPaymentPercent, {
                            val home = principal.toDoubleOrNull() ?: 0.0
                            val requestedPercent = it.toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                            val clampedPercent = requestedPercent.coerceIn(0.0, 100.0)
                            downPaymentPercent = if (it.isEmpty()) "" else if (requestedPercent > 100.0) "100" else it
                            downPayment = if (it.isEmpty()) "" else if (home > 0.0) {
                                formatMortgageAmount(home * clampedPercent / 100.0)
                            } else {
                                ""
                            }
                        }, suffix = "%", modifier = Modifier.weight(1f), showInfo = false)
                    }
                }
                item { MortgageTermField(months, { months = it }) }
                item { FinanceField(stringResource(R.string.interest_rate), rate, { rate = it }, suffix = "%") }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinanceField(stringResource(R.string.property_tax), propertyTax, { propertyTax = it }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                        FinanceField(stringResource(R.string.pmi), pmi, { pmi = it }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinanceField(stringResource(R.string.hoa_fees), hoaFees, { hoaFees = it }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                        FinanceField(stringResource(R.string.home_insurance), homeInsurance, { homeInsurance = it }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                    }
                }
            } else {
                item { FinanceField(amountLabel, principal, { principal = it }, prefix = "${currency.symbol} ") }
                item { FinanceField(stringResource(R.string.interest_rate), rate, { rate = it }, suffix = "%") }
                item { LoanTermField(months, { months = it }, termUnit, { termUnit = it }, termMenuOpen, { termMenuOpen = it }, label = if (isDeposit) stringResource(R.string.tenure) else stringResource(R.string.loan_term)) }
                if (type == CalculatorType.FD) {
                    item { DepositCompoundingField(compounding, { compounding = it }) }
                }
                if (type == CalculatorType.PERSONAL || type == CalculatorType.AUTO || isDeposit) {
                    item { StartDateField(startDateMillis, onClick = { datePickerOpen = true }) }
                }
            }
            item { error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) } }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { principal = ""; rate = ""; months = ""; termUnit = if (isDeposit || type == CalculatorType.BUSINESS) "Month" else "Year"; compounding = "1"; downPayment = ""; downPaymentPercent = ""; propertyTax = ""; pmi = ""; hoaFees = ""; homeInsurance = ""; currencyCode = "GBP"; startDateMillis = System.currentTimeMillis(); error = null }, modifier = Modifier.weight(1f).heightIn(min = 54.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp), shape = RoundedCornerShape(27.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBD19))) { Text(stringResource(R.string.reset_fields), textAlign = TextAlign.Center, maxLines = 2) }
                    Button(onClick = {
                    val p = principal.toDoubleOrNull() ?: 0.0
                    val r = rate.toDoubleOrNull() ?: 0.0
                    val m = (months.toIntOrNull() ?: 1) * if (type == CalculatorType.MORTGAGE || termUnit == "Year") 12 else 1
                    if (p <= 0 || r < 0 || m <= 0) {
                        error = invalidInputError
                    } else if (type == CalculatorType.MORTGAGE) {
                        val down = (downPayment.toDoubleOrNull() ?: 0.0).coerceIn(0.0, p)
                        val financed = (p - down).coerceAtLeast(0.0)
                        if (financed <= 0.0) {
                            error = downPaymentError
                        } else {
                            error = null
                            val result = FinancialCalculator.loan(LoanInput(financed, r, m))
                            val taxMonthly = (propertyTax.toDoubleOrNull() ?: 0.0) / 12.0
                            val pmiMonthly = (pmi.toDoubleOrNull() ?: 0.0) / 12.0
                            val hoaMonthly = hoaFees.toDoubleOrNull() ?: 0.0
                            val insuranceMonthly = (homeInsurance.toDoubleOrNull() ?: 0.0) / 12.0
                            val extraMonthly = taxMonthly + pmiMonthly + hoaMonthly + insuranceMonthly
                            val totalMonthly = result.monthlyPayment + extraMonthly
                            val totalPayment = result.totalPayment + extraMonthly * m
                            val summary = "Home Price=$p|Down Payment=$down|Interest Rate=$r|Loan Term=$m|Property Tax=${propertyTax.toDoubleOrNull() ?: 0.0}|PMI=${pmi.toDoubleOrNull() ?: 0.0}|HOA Fees=${hoaFees.toDoubleOrNull() ?: 0.0}|Home insurance=${homeInsurance.toDoubleOrNull() ?: 0.0}|Principal & Interest=${result.monthlyPayment}|Monthly payment=$totalMonthly|Total payment=$totalPayment|Total interest=${result.totalInterest}"
                            viewModel.save(type, "homePrice=$p;downPayment=$down;rate=$r;months=$m;propertyTax=$propertyTax;pmi=$pmi;hoaFees=$hoaFees;homeInsurance=$homeInsurance;currency=${currency.code}", summary, onSaved)
                        }
                    } else if (type.category == "Loans") {
                        error = null
                        val result = FinancialCalculator.loan(LoanInput(p, r, m))
                        val startDatePart = if (type == CalculatorType.PERSONAL || type == CalculatorType.AUTO) ";startDate=$startDateMillis" else ""
                        val frequencyPart = if (type == CalculatorType.BUSINESS) ";frequency=$termUnit" else ""
                        viewModel.save(type, "principal=$p;rate=$r;months=$m$startDatePart$frequencyPart;currency=${currency.code}", FinancialCalculator.summary(type, loan = result), onSaved)
                    } else {
                        error = null
                        val compoundPeriods = compounding.toIntOrNull() ?: 4
                        val depositInput = if (type == CalculatorType.RD) {
                            DepositInput(0.0, r, m, monthlyContribution = p, compounding = compoundPeriods)
                        } else {
                            DepositInput(p, r, m, compounding = compoundPeriods)
                        }
                        val result = FinancialCalculator.deposit(depositInput)
                        val amountKey = if (type == CalculatorType.RD) "monthlyContribution" else "principal"
                        viewModel.save(type, "$amountKey=$p;rate=$r;months=$m;compounding=$compoundPeriods;startDate=$startDateMillis;currency=${currency.code}", FinancialCalculator.summary(type, deposit = result), onSaved)
                    }
                    }, modifier = Modifier.weight(1f).heightIn(min = 54.dp), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp), shape = RoundedCornerShape(27.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16B2D7))) { Text(stringResource(R.string.calculate), textAlign = TextAlign.Center, maxLines = 2) }
                }
            }
            }
        }
    }
    if (datePickerOpen) {
        val datePickerState = androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = startDateMillis)
        DatePickerDialog(
            onDismissRequest = { datePickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { startDateMillis = it }
                    datePickerOpen = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = { TextButton(onClick = { datePickerOpen = false }) { Text(stringResource(R.string.cancel)) } },
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun FieldLabel(label: String, showInfo: Boolean = true, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        if (showInfo) Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF18A9D0), modifier = Modifier.padding(start = 4.dp).size(17.dp))
    }
}

private fun formatMortgageAmount(value: Double): String = String.format(Locale.US, "%.2f", value.coerceAtLeast(0.0))

private fun formatMortgagePercent(value: Double): String = String.format(Locale.US, "%.2f", value.coerceIn(0.0, 100.0))

@Composable
private fun StartDateField(startDateMillis: Long, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(stringResource(R.string.start_date), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, Color.White),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF8BA1AA)),
        ) {
            Text(DateFormat.format("dd/MM/yyyy", startDateMillis).toString(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.choose_start_date), tint = Color(0xFF6E777B))
        }
    }
}

@Composable
private fun termUnitLabel(unit: String): String = stringResource(
    if (unit == "Year") R.string.year else R.string.month,
)

@Composable
private fun LoanTermField(
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    label: String = "",
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(it.filter(Char::isDigit)) },
                placeholder = { Text("0", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9BAEB6)) },
                modifier = Modifier.weight(1f).height(56.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedTextColor = Color(0xFF10233A),
                    focusedTextColor = Color(0xFF10233A),
                ),
            )
            Box(Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { onMenuOpenChange(true) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(30.dp),
                    border = BorderStroke(1.dp, Color.White),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF8BA1AA)),
                ) {
                    Text(termUnitLabel(unit), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(R.string.choose_term_unit), tint = Color(0xFF6E777B))
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { onMenuOpenChange(false) }) {
                    listOf("Year", "Month").forEach { option ->
                        DropdownMenuItem(text = { Text(termUnitLabel(option)) }, onClick = { onUnitChange(option); onMenuOpenChange(false) })
                    }
                }
            }
        }
    }
}

@Composable
private fun DepositCompoundingField(value: String, onValueChange: (String) -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(stringResource(R.string.compounding_frequency))
        Box {
            OutlinedButton(
                onClick = { menuOpen = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(30.dp),
                border = BorderStroke(1.dp, Color.White),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF8BA1AA)),
            ) {
                Text(value, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(R.string.choose_compounding), tint = Color(0xFF6E777B))
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                listOf("1", "2", "4", "12").forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = { onValueChange(option); menuOpen = false })
                }
            }
        }
    }
}

@Composable
private fun MortgageTermField(value: String, onValueChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(stringResource(R.string.loan_term))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter(Char::isDigit)) },
            placeholder = { Text("0", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9BAEB6)) },
            suffix = { Text(stringResource(R.string.year), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8BA1AA)) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedTextColor = Color(0xFF10233A),
                focusedTextColor = Color(0xFF10233A),
            ),
        )
    }
}

@Composable
private fun FinanceField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String? = null,
    suffix: String? = null,
    modifier: Modifier = Modifier,
    showInfo: Boolean = true,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label, showInfo)
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter { char -> char.isDigit() || char == '.' }) },
            placeholder = { Text("0", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF9BAEB6)) },
            prefix = { prefix?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8BA1AA)) } },
            suffix = { suffix?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8BA1AA)) } },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(30.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedTextColor = Color(0xFF10233A),
                focusedTextColor = Color(0xFF10233A),
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(id: Long, onBack: () -> Unit, onCompare: (CalculatorType?) -> Unit, onShare: (String, String) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    val item = history.firstOrNull { it.id == id }
    val entries = summaryEntries(item?.resultSummary.orEmpty())
    val currencyCode = loanCurrencyCode(item?.inputJson.orEmpty())
    val startDate = inputValue(item?.inputJson.orEmpty(), "startDate")?.toLongOrNull()
    val termMonths = inputValue(item?.inputJson.orEmpty(), "months")?.toIntOrNull()
    val payoffMonths = entries.firstOrNull { it.first.equals("Payoff months", ignoreCase = true) }?.second?.toDoubleOrNull()?.toInt()
    val calculatorTitle = item?.let { calculatorLabel(CalculatorType.fromKey(it.calculatorType)) }
    Scaffold(topBar = { FinanceTopBar(stringResource(R.string.calculation_result), calculatorTitle, onBack, compact = true) }) { padding ->
        LazyColumn(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Text(stringResource(R.string.result_after_calculation), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        if ((item?.category == "Loans" || item?.category == "Deposits") && startDate != null) {
                            ResultDateRow(stringResource(R.string.start_date), formatDate(startDate))
                        }
                        entries.forEachIndexed { index, (label, value) ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(resultLabel(label), modifier = Modifier.weight(0.52f).padding(end = 12.dp), color = Color(0xFF17A8CD), style = MaterialTheme.typography.titleMedium)
                                Text(resultValue(label, value, currencyCode), modifier = Modifier.weight(0.48f), color = Color(0xFF10233A), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                            }
                        }
                        if (item?.category == "Loans" && startDate != null && payoffMonths != null) {
                            Divider(color = Color(0xFFD7E2E6))
                            ResultDateRow(stringResource(R.string.payoff_date), payoffDate(startDate, payoffMonths))
                        } else if (item?.category == "Deposits" && startDate != null && termMonths != null) {
                            Divider(color = Color(0xFFD7E2E6))
                            ResultDateRow(stringResource(R.string.maturity_date), payoffDate(startDate, termMonths))
                        }
                    }
                }
            }
            item {
                Button(onClick = { onCompare(item?.let { CalculatorType.fromKey(it.calculatorType) }) }, modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp), shape = RoundedCornerShape(30.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16B2D7))) { Text(stringResource(R.string.add_to_compare), textAlign = TextAlign.Center, maxLines = 2) }
            }
            item {
                Button(onClick = { item?.let { onShare(calculatorTitle ?: it.title, it.resultSummary) } }, modifier = Modifier.fillMaxWidth().heightIn(min = 58.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp), shape = RoundedCornerShape(30.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBD19))) { Icon(Icons.Default.Share, null); Spacer(Modifier.size(8.dp)); Text(stringResource(R.string.share_pdf), textAlign = TextAlign.Center, maxLines = 2) }
            }
        }
    }
}

@Composable
private fun resultLabel(label: String): String = when (label.lowercase(Locale.US)) {
    "monthly payment" -> stringResource(R.string.monthly_payment)
    "total payment" -> stringResource(R.string.total_payment)
    "total interest" -> stringResource(R.string.total_interest_paid)
    "principal & interest" -> stringResource(R.string.principal_interest)
    "interest earned" -> stringResource(R.string.interest_earned)
    "maturity value" -> stringResource(R.string.maturity_value)
    "total deposited" -> stringResource(R.string.total_deposited)
    "total invested" -> stringResource(R.string.total_invested)
    "payoff months" -> stringResource(R.string.duration)
    else -> label
}

@Composable
private fun ResultDateRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(0.52f).padding(end = 12.dp), color = Color(0xFF17A8CD), style = MaterialTheme.typography.titleMedium)
        Text(value, modifier = Modifier.weight(0.48f), color = Color(0xFF10233A), fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

@Composable
fun HistoryScreen(onBack: () -> Unit, onOpen: (Long) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    Scaffold(topBar = { SimpleTopBar(stringResource(R.string.history), onBack) }) { padding ->
        if (history.isEmpty()) Column(Modifier.fillMaxSize().padding(padding), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text(stringResource(R.string.no_saved_calculations)) }
        else LazyColumn(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(history) { item ->
                Card(modifier = Modifier.fillMaxWidth().noRippleClickable { onOpen(item.id) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(calculatorLabel(CalculatorType.fromKey(item.calculatorType)), fontWeight = FontWeight.Bold)
                        Text(localizedSummary(item.resultSummary), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(title: String, onBack: () -> Unit) { TopAppBar(title = { Text(title) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, stringResource(R.string.back)) } }) }

@Composable
private fun localizedSummary(summary: String): String {
    val result = StringBuilder()
    for ((index, entry) in summaryEntries(summary).withIndex()) {
        if (index > 0) result.append("  •  ")
        result.append(localizedSummaryLabel(entry.first)).append('=').append(entry.second)
    }
    return result.toString()
}

@Composable
private fun localizedSummaryLabel(label: String): String = when (label.lowercase(Locale.US)) {
    "monthly payment" -> stringResource(R.string.monthly_payment)
    "total payment" -> stringResource(R.string.total_payment)
    "total interest" -> stringResource(R.string.total_interest_paid)
    "principal & interest" -> stringResource(R.string.principal_interest)
    "maturity value" -> stringResource(R.string.maturity_value)
    "total deposited" -> stringResource(R.string.total_deposited)
    "total invested" -> stringResource(R.string.total_invested)
    "interest earned" -> stringResource(R.string.interest_earned)
    "payoff months" -> stringResource(R.string.duration)
    else -> label
}
