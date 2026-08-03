package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.brian.base_application.language.LanguageActivity
import com.loancaculator.core.IapOpener
import com.loancaculator.core.AppStorage
import com.loancaculator.core.MainActivity
import com.loancaculator.ui.components.findActivity
import com.loancaculator.advertisement.NativeAdSlot
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star

private val baseCurrencyOptions = listOf("GBP", "USD", "EUR", "JPY", "VND")

@Composable
fun FinanceSettingsScreen(onNavigate: (String) -> Unit, viewModel: FinanceViewModel) {
    val context = LocalContext.current
    var baseCurrency by remember { mutableStateOf(AppStorage.exchangeBaseCurrency(context)) }
    var baseCurrencyMenuOpen by remember { mutableStateOf(false) }
    Scaffold(topBar = { FinanceTopBar("Settings", "Preferences and account", compact = true) }, bottomBar = { FinanceBottomBar("setting", onNavigate) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NativeAdSlot("native_settings", modifier = Modifier.fillMaxWidth(), isSmall = true)
            SettingRow(Icons.Default.Star, "Premium", "Remove ads and unlock every feature") { IapOpener.open(context, "settings") }
            Box {
                SettingRow(
                    Icons.Default.Build,
                    "Exchange base currency",
                    "${loanCurrency(baseCurrency).code} (${loanCurrency(baseCurrency).name})",
                ) { baseCurrencyMenuOpen = true }
                DropdownMenu(
                    expanded = baseCurrencyMenuOpen,
                    onDismissRequest = { baseCurrencyMenuOpen = false },
                ) {
                    baseCurrencyOptions.forEach { code ->
                        val currency = loanCurrency(code)
                        DropdownMenuItem(
                            text = { Text("${currency.code} (${currency.name})") },
                            onClick = {
                                baseCurrency = code
                                AppStorage.setExchangeBaseCurrency(context, code)
                                baseCurrencyMenuOpen = false
                            },
                        )
                    }
                }
            }
            SettingRow(Icons.Default.Settings, "Language", "System language") { (context.findActivity() as? FragmentActivity)?.let { LanguageActivity.start(it, MainActivity::class.java) } }
            SettingRow(Icons.Default.Notifications, "Notifications", "Calculation reminders are enabled")
            SettingRow(Icons.Default.Lock, "Privacy", "Your calculations stay on this device")
            Button(onClick = { viewModel.clearHistory() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Clear calculation history")
            }
            Spacer(Modifier.weight(1f))
            Text(
                "Loan Calculation Plus\nVersion 1.0",
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    Card(onClick = { onClick?.invoke() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color(0xFFE4F5FA), androidx.compose.foundation.shape.CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF18A9D0), modifier = Modifier.size(21.dp))
            }
            Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            if (onClick != null) Icon(Icons.Default.ArrowForward, "Open", tint = MaterialTheme.colorScheme.secondary)
        }
    }
}
