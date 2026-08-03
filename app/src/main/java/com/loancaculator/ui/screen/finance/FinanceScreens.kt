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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loancaculator.advertisement.AdManager
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.core.IapOpener
import com.loancaculator.ui.components.findActivity
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
            item { HomeSectionTitle("Investment") }
            item { NativeAdSlot("native_home", modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(240.dp), isSmall = false) }
            item { InvestmentRow(CalculatorType.FD, ::openCalculator) }
            item { InvestmentRow(CalculatorType.RD, ::openCalculator) }
            item {
                Button(
                    onClick = { IapOpener.open(context, "home") },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFD18A00), Gold, Color(0xFFFFD96A), Gold, Color(0xFFD18A00))),
                            RoundedCornerShape(24.dp),
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color(0xFF4A2B00)),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp),
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Remove ads and unlock premium", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
            item {
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { onNavigate("history") }, contentPadding = PaddingValues(0.dp)) {
                        Text("View all \u2192", color = Color(0xFF16B2D7), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            if (recentHistory.isEmpty()) {
                item {
                    Text(
                        "No calculations yet \u2014 try a loan or deposit calculator.",
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
        onClick = { if (selectionMode) onToggleSelected() else onOpen() },
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
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
                        if (type == CalculatorType.MORTGAGE) "Mortgages" else item.title,
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
                        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
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
    Card(onClick = { onClick(type) }, modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(66.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            DepositSpriteIcon(if (type == CalculatorType.FD) 0 else 1, Modifier.size(46.dp))
            Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(if (type == CalculatorType.FD) "FD" else "RD", fontWeight = FontWeight.Bold); Text(type.label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            FinanceArrow()
        }
    }
}

@Composable
private fun CalculatorCard(type: CalculatorType, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().height(116.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FinanceLoanIcon(type.iconIndex(), Modifier.size(58.dp))
                FinanceArrow()
            }
            Text(type.label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
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
    var principal by remember { mutableStateOf(if (type.category == "Deposits") "" else "100000000") }
    var rate by remember { mutableStateOf(if (type.category == "Deposits") "" else "8.5") }
    var months by remember { mutableStateOf(if (type.category == "Deposits") "" else "5") }
    var downPayment by remember { mutableStateOf("0") }
    var downPaymentPercent by remember { mutableStateOf("0") }
    var propertyTax by remember { mutableStateOf("0") }
    var pmi by remember { mutableStateOf("0") }
    var hoaFees by remember { mutableStateOf("0") }
    var homeInsurance by remember { mutableStateOf("0") }
    var compounding by remember { mutableStateOf("4") }
    var error by remember { mutableStateOf<String?>(null) }
    var currencyCode by remember { mutableStateOf("GBP") }
    var currencyMenuOpen by remember { mutableStateOf(false) }
    var termUnit by remember { mutableStateOf(if (type.category == "Deposits") "Month" else "Year") }
    var termMenuOpen by remember { mutableStateOf(false) }
    var startDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var datePickerOpen by remember { mutableStateOf(false) }
    val currency = loanCurrency(currencyCode)
    val isLoan = type.category == "Loans"
    val isDeposit = type.category == "Deposits"
    val amountLabel = when {
        type == CalculatorType.MORTGAGE -> "Home Price"
        type == CalculatorType.RD -> "Monthly Deposit"
        isLoan -> "Loan Amount"
        else -> "Investment Amount"
    }
    val calculatorAdHeight = if (type == CalculatorType.MORTGAGE) 260.dp else 140.dp
    Scaffold(topBar = { FinanceTopBar(type.label, "Enter details to estimate your result", onBack, compact = true) }) { padding ->
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
                    FieldLabel(amountLabel)
                    Box {
                        OutlinedButton(onClick = { currencyMenuOpen = true }, shape = RoundedCornerShape(24.dp), colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF10233A))) {
                            Text("${currency.flag}  ${currency.code}")
                            FinanceArrow()
                        }
                        DropdownMenu(expanded = currencyMenuOpen, onDismissRequest = { currencyMenuOpen = false }) {
                            loanCurrencies.forEach { option ->
                                DropdownMenuItem(
                                    leadingIcon = { Text(option.flag) },
                                    text = { Text("${option.symbol}  ${option.code} - ${option.name}") },
                                    onClick = { currencyCode = option.code; currencyMenuOpen = false }
                                )
                            }
                        }
                    }
                }
            }
            if (type == CalculatorType.MORTGAGE) {
                item { FinanceField("Home Price", principal, { principal = it }, prefix = "${currency.symbol} ") }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinanceField("Down Payment", downPayment, {
                            downPayment = it
                            val home = principal.toDoubleOrNull() ?: 0.0
                            downPaymentPercent = if (home > 0 && it.isNotEmpty()) "%.2f".format(Locale.US, (it.toDoubleOrNull() ?: 0.0) / home * 100.0) else ""
                        }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                        FinanceField("Down Payment", downPaymentPercent, {
                            downPaymentPercent = it
                            val home = principal.toDoubleOrNull() ?: 0.0
                            downPayment = if (home > 0 && it.isNotEmpty()) "%.2f".format(Locale.US, home * (it.toDoubleOrNull() ?: 0.0) / 100.0) else ""
                        }, suffix = "%", modifier = Modifier.weight(1f), showInfo = false)
                    }
                }
                item { MortgageTermField(months, { months = it }) }
                item { FinanceField("Interest Rate", rate, { rate = it }, suffix = "%") }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinanceField("Property Tax", propertyTax, { propertyTax = it }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                        FinanceField("PMI", pmi, { pmi = it }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                    }
                }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FinanceField("HOA Fees", hoaFees, { hoaFees = it }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                        FinanceField("Home insurance", homeInsurance, { homeInsurance = it }, prefix = "${currency.symbol} ", modifier = Modifier.weight(1f), showInfo = false)
                    }
                }
            } else {
                item { FinanceField(amountLabel, principal, { principal = it }, prefix = "${currency.symbol} ") }
                item { FinanceField("Interest Rate", rate, { rate = it }, suffix = "%") }
                item { LoanTermField(months, { months = it }, termUnit, { termUnit = it }, termMenuOpen, { termMenuOpen = it }, label = if (isDeposit) "Tenure" else "Loan Term") }
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
                    Button(onClick = { principal = if (isDeposit) "" else "100000000"; rate = if (isDeposit) "" else "8.5"; months = if (isDeposit) "" else "5"; termUnit = if (isDeposit) "Month" else "Year"; compounding = "4"; downPayment = "0"; downPaymentPercent = "0"; propertyTax = "0"; pmi = "0"; hoaFees = "0"; homeInsurance = "0"; currencyCode = "GBP"; startDateMillis = System.currentTimeMillis(); error = null }, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(27.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBD19))) { Text("Reset Fields") }
                    Button(onClick = {
                    val p = principal.toDoubleOrNull() ?: 0.0
                    val r = rate.toDoubleOrNull() ?: 0.0
                    val m = (months.toIntOrNull() ?: 1) * if (type == CalculatorType.MORTGAGE || termUnit == "Year") 12 else 1
                    if (p <= 0 || r < 0 || m <= 0) {
                        error = "Enter a valid amount, rate and term."
                    } else if (type == CalculatorType.MORTGAGE) {
                        val down = downPayment.toDoubleOrNull() ?: 0.0
                        val financed = (p - down).coerceAtLeast(0.0)
                        if (financed <= 0.0) {
                            error = "Down payment must be lower than the home price."
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
                        viewModel.save(type, "principal=$p;rate=$r;months=$m$startDatePart;currency=${currency.code}", FinancialCalculator.summary(type, loan = result), onSaved)
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
                    }, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(27.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16B2D7))) { Text("Calculate") }
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
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { datePickerOpen = false }) { Text("Cancel") } },
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun FieldLabel(label: String, showInfo: Boolean = true) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        if (showInfo) Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF18A9D0), modifier = Modifier.padding(start = 4.dp).size(17.dp))
    }
}

@Composable
private fun StartDateField(startDateMillis: Long, onClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Start Date", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, Color.White),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Color(0xFF8BA1AA)),
        ) {
            Text(DateFormat.format("dd/MM/yyyy", startDateMillis).toString(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Icon(Icons.Default.DateRange, contentDescription = "Choose start date", tint = Color(0xFF6E777B))
        }
    }
}

@Composable
private fun LoanTermField(
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    onUnitChange: (String) -> Unit,
    menuOpen: Boolean,
    onMenuOpenChange: (Boolean) -> Unit,
    label: String = "Loan Term",
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        FieldLabel(label)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(it.filter(Char::isDigit)) },
                placeholder = { Text(label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8BA1AA)) },
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
                    Text(unit, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose term unit", tint = Color(0xFF6E777B))
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { onMenuOpenChange(false) }) {
                    listOf("Year", "Month").forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { onUnitChange(option); onMenuOpenChange(false) })
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
        FieldLabel("The number of times interest")
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
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Choose compounding frequency", tint = Color(0xFF6E777B))
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
        FieldLabel("Loan Term")
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it.filter(Char::isDigit)) },
            placeholder = { Text("Loan Term", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8BA1AA)) },
            suffix = { Text("Year", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8BA1AA)) },
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
            placeholder = { Text(label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8BA1AA)) },
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
fun ResultScreen(id: Long, onBack: () -> Unit, onCompare: () -> Unit, onShare: (String, String) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    val item = history.firstOrNull { it.id == id }
    val entries = summaryEntries(item?.resultSummary.orEmpty())
    val currencyCode = loanCurrencyCode(item?.inputJson.orEmpty())
    val startDate = inputValue(item?.inputJson.orEmpty(), "startDate")?.toLongOrNull()
    val termMonths = inputValue(item?.inputJson.orEmpty(), "months")?.toIntOrNull()
    val payoffMonths = entries.firstOrNull { it.first.equals("Payoff months", ignoreCase = true) }?.second?.toDoubleOrNull()?.toInt()
    Scaffold(topBar = { FinanceTopBar("Calculation result", item?.title, onBack, compact = true) }) { padding ->
        LazyColumn(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Text("Result after calculation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        if ((item?.category == "Loans" || item?.category == "Deposits") && startDate != null) {
                            ResultDateRow("Start Date", formatDate(startDate))
                        }
                        entries.forEachIndexed { index, (label, value) ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(label, color = Color(0xFF17A8CD), style = MaterialTheme.typography.titleMedium)
                                Text(resultValue(label, value, currencyCode), color = Color(0xFF10233A), fontWeight = FontWeight.Bold)
                            }
                        }
                        if (item?.category == "Loans" && startDate != null && payoffMonths != null) {
                            Divider(color = Color(0xFFD7E2E6))
                            ResultDateRow("Pay-off Date", payoffDate(startDate, payoffMonths))
                        } else if (item?.category == "Deposits" && startDate != null && termMonths != null) {
                            Divider(color = Color(0xFFD7E2E6))
                            ResultDateRow("Maturity Date", payoffDate(startDate, termMonths))
                        }
                    }
                }
            }
            item {
                Button(onClick = onCompare, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(30.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16B2D7))) { Text("Add to compare list") }
            }
            item {
                Button(onClick = { item?.let { onShare(it.title, it.resultSummary) } }, modifier = Modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(30.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBD19))) { Icon(Icons.Default.Share, null); Spacer(Modifier.size(8.dp)); Text("Share PDF") }
            }
        }
    }
}

@Composable
private fun ResultDateRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color(0xFF17A8CD), style = MaterialTheme.typography.titleMedium)
        Text(value, color = Color(0xFF10233A), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HistoryScreen(onBack: () -> Unit, onOpen: (Long) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    Scaffold(topBar = { SimpleTopBar("History", onBack) }) { padding ->
        if (history.isEmpty()) Column(Modifier.fillMaxSize().padding(padding), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text("No saved calculations yet") }
        else LazyColumn(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(history) { item ->
                Card(onClick = { onOpen(item.id) }, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(item.title, fontWeight = FontWeight.Bold); Text(item.resultSummary.replace("|", "  •  "), color = MaterialTheme.colorScheme.onSurfaceVariant) } }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(title: String, onBack: () -> Unit) { TopAppBar(title = { Text(title) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }) }
