package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.loancaculator.R
import com.loancaculator.data.finance.CalculatorType
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class FinanceTab(val route: String, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector)

data class LoanCurrency(val code: String, val symbol: String, val name: String, val flag: String)

val loanCurrencies = listOf(
    LoanCurrency("GBP", "\u00A3", "British Pound", "\uD83C\uDDEC\uD83C\uDDE7"),
    LoanCurrency("USD", "$", "US Dollar", "\uD83C\uDDFA\uD83C\uDDF8"),
    LoanCurrency("EUR", "\u20AC", "Euro", "\uD83C\uDDEA\uD83C\uDDFA"),
    LoanCurrency("VND", "\u20AB", "Vietnamese Dong", "\uD83C\uDDFB\uD83C\uDDF3"),
    LoanCurrency("JPY", "\u00A5", "Japanese Yen", "\uD83C\uDDEF\uD83C\uDDF5"),
    LoanCurrency("AUD", "A$", "Australian Dollar", "\uD83C\uDDE6\uD83C\uDDFA"),
    LoanCurrency("CAD", "C$", "Canadian Dollar", "\uD83C\uDDE8\uD83C\uDDE6"),
    LoanCurrency("CHF", "CHF", "Swiss Franc", "\uD83C\uDDE8\uD83C\uDDED"),
    LoanCurrency("CNY", "CN\u00A5", "Chinese Yuan", "\uD83C\uDDE8\uD83C\uDDF3"),
    LoanCurrency("HKD", "HK$", "Hong Kong Dollar", "\uD83C\uDDED\uD83C\uDDF0"),
    LoanCurrency("INR", "\u20B9", "Indian Rupee", "\uD83C\uDDEE\uD83C\uDDF3"),
    LoanCurrency("IDR", "Rp", "Indonesian Rupiah", "\uD83C\uDDEE\uD83C\uDDE9"),
    LoanCurrency("KRW", "\u20A9", "South Korean Won", "\uD83C\uDDF0\uD83C\uDDF7"),
    LoanCurrency("MYR", "RM", "Malaysian Ringgit", "\uD83C\uDDF2\uD83C\uDDFE"),
    LoanCurrency("PHP", "\u20B1", "Philippine Peso", "\uD83C\uDDF5\uD83C\uDDED"),
    LoanCurrency("SGD", "S$", "Singapore Dollar", "\uD83C\uDDF8\uD83C\uDDEC"),
    LoanCurrency("THB", "\u0E3F", "Thai Baht", "\uD83C\uDDF9\uD83C\uDDED"),
    LoanCurrency("ZAR", "R", "South African Rand", "\uD83C\uDDE6\uD83C\uDDFF"),
)

fun loanCurrency(code: String): LoanCurrency = loanCurrencies.firstOrNull { it.code == code } ?: loanCurrencies.first()

@Composable
fun currencyName(code: String): String = stringResource(
    when (code) {
        "GBP" -> R.string.currency_gbp
        "USD" -> R.string.currency_usd
        "EUR" -> R.string.currency_eur
        "VND" -> R.string.currency_vnd
        "JPY" -> R.string.currency_jpy
        "AUD" -> R.string.currency_aud
        "CAD" -> R.string.currency_cad
        "CHF" -> R.string.currency_chf
        "CNY" -> R.string.currency_cny
        "HKD" -> R.string.currency_hkd
        "INR" -> R.string.currency_inr
        "IDR" -> R.string.currency_idr
        "KRW" -> R.string.currency_krw
        "MYR" -> R.string.currency_myr
        "PHP" -> R.string.currency_php
        "SGD" -> R.string.currency_sgd
        "THB" -> R.string.currency_thb
        "ZAR" -> R.string.currency_zar
        else -> R.string.currency_gbp
    },
)

@Composable
fun calculatorLabel(type: CalculatorType): String = stringResource(
    when (type) {
        CalculatorType.PERSONAL -> R.string.personal_loan
        CalculatorType.BUSINESS -> R.string.business_loan
        CalculatorType.AUTO -> R.string.auto_loan
        CalculatorType.MORTGAGE -> R.string.mortgage
        CalculatorType.FD -> R.string.fixed_deposit
        CalculatorType.RD -> R.string.recurring_deposit
    },
)

fun loanCurrencyCode(input: String): String = input.split(";")
    .firstOrNull { it.startsWith("currency=") }
    ?.substringAfter("=")
    ?.let(::loanCurrency)
    ?.code
    ?: "GBP"

private val financeTabs = listOf(
    FinanceTab("home", R.string.nav_home, Icons.Default.Home),
    FinanceTab("tools", R.string.nav_tools, Icons.Default.Build),
    FinanceTab("compare", R.string.nav_compare, Icons.Default.List),
    FinanceTab("setting", R.string.nav_settings, Icons.Default.Settings),
)

@Composable
fun FinanceBottomBar(current: String, onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        financeTabs.forEach { tab ->
            val route = if (tab.label == "Setting") "setting" else tab.label.lowercase()
            val selected = current == route
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigate(route) }
                    .offset(y = 1.dp)
                    .padding(vertical = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(Modifier.size(22.dp).background(if (selected) Color(0xFFE4F5FA) else Color(0xFFF1F4F5), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(tab.icon, contentDescription = tab.label, tint = if (selected) Color(0xFF18A9D0) else Color(0xFF9BA7AD), modifier = Modifier.size(15.dp))
                }
                Text(
                    tab.label,
                    fontSize = 9.sp,
                    lineHeight = 9.sp,
                    color = if (selected) Color(0xFF18A9D0) else Color(0xFF9BA7AD),
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun FinanceHero(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(176.dp)) {
        Image(painterResource(R.drawable.finance_hero), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
        Text(stringResource(R.string.app_name), modifier = Modifier.align(Alignment.TopCenter).padding(top = 28.dp), color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun FinanceTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    compact: Boolean = false,
    heightOverride: Dp? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val barHeight = heightOverride ?: if (compact) {
        if (title.length > 22 || (subtitle?.length ?: 0) > 28) 104.dp else 88.dp
    } else 138.dp
    val backSize = if (compact) 36.dp else 48.dp
    val iconSize = if (compact) 20.dp else 24.dp
    val titleSize = if (compact) 20.sp else 24.sp

    Box(
        Modifier
            .fillMaxWidth()
            .height(barHeight)
            .background(Color(0xFF12A9D0))
    ) {
        Image(
            painter = painterResource(R.drawable.finance_hero),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = if (compact || heightOverride != null) ContentScale.FillBounds else ContentScale.Crop
        )
        onBack?.let { callback ->
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = if (compact) 12.dp else 16.dp)
                    .size(backSize)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = callback),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color(0xFF18A9D0),
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(if (compact) 6.dp else 10.dp),
            horizontalArrangement = Arrangement.End,
            content = actions
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = if (onBack != null) 58.dp else 24.dp,
                    end = 58.dp,
                    bottom = if (compact) 8.dp else 22.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = titleSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            subtitle?.let {
                Text(
                    text = it,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun FinanceArrow() { Icon(Icons.Default.ArrowForward, contentDescription = stringResource(R.string.open), tint = Color(0xFFB7CBD3), modifier = Modifier.size(20.dp)) }

fun money(value: Double): String = NumberFormat.getCurrencyInstance(Locale.UK).apply { maximumFractionDigits = 2 }.format(value)

fun money(value: Double, currencyCode: String): String {
    val currency = loanCurrency(currencyCode)
    return "${currency.symbol}${String.format(Locale.US, "%,.2f", value)}"
}

fun summaryEntries(summary: String): List<Pair<String, String>> = summary.split("|").mapNotNull { item ->
    val parts = item.split("=", limit = 2)
    if (parts.size == 2) parts[0] to parts[1] else null
}

fun inputValue(input: String, key: String): String? = input.split(";")
    .firstOrNull { it.startsWith("$key=") }
    ?.substringAfter("=")

fun formatDate(millis: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date(millis))

fun payoffDate(startMillis: Long, months: Int): String = Calendar.getInstance().apply {
    timeInMillis = startMillis
    add(Calendar.MONTH, months)
}.timeInMillis.let(::formatDate)

@Composable
fun resultValue(label: String, raw: String, currencyCode: String = "GBP"): String = when {
    label.contains("rate", ignoreCase = true) -> "$raw%"
    label.equals("Loan Term", ignoreCase = true) -> raw.toDoubleOrNull()?.toInt()?.let {
        if (it % 12 == 0) stringResource(R.string.duration_years, it / 12)
        else stringResource(R.string.duration_months, it)
    } ?: raw
    label.contains("months", ignoreCase = true) -> raw.toDoubleOrNull()?.toInt()?.let { stringResource(R.string.duration_months, it) } ?: raw
    else -> raw.toDoubleOrNull()?.let { money(it, currencyCode) } ?: raw
}
