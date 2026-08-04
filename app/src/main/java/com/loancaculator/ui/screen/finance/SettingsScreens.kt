package com.loancaculator.ui.screen.finance

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.loancaculator.core.IapOpener
import com.loancaculator.core.AppStorage
import com.loancaculator.R
import com.loancaculator.ui.components.findActivity
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.ui.screen.language.MyLanguageActivity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star

@Composable
fun FinanceSettingsScreen(onNavigate: (String) -> Unit, onCurrency: () -> Unit, viewModel: FinanceViewModel) {
    val context = LocalContext.current
    val baseCurrency = AppStorage.exchangeBaseCurrency(context)
    Scaffold(topBar = { FinanceTopBar(stringResource(R.string.nav_settings), compact = true) }, bottomBar = { FinanceBottomBar("setting", onNavigate) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                NativeAdSlot("native_settings", modifier = Modifier.fillMaxWidth().heightIn(max = 72.dp), isSmall = true)
                SettingRow(Icons.Default.Star, stringResource(R.string.premium), stringResource(R.string.premium_description)) { IapOpener.open(context, "settings") }
                SettingRow(
                    Icons.Default.Build,
                    stringResource(R.string.exchange_base_currency),
                    "${loanCurrency(baseCurrency).flag} ${loanCurrency(baseCurrency).code} (${currencyName(baseCurrency)})",
                    onClick = onCurrency,
                )
                SettingRow(Icons.Default.Settings, stringResource(R.string.language), stringResource(R.string.language_description)) {
                    context.findActivity()?.startActivity(
                        Intent(context, MyLanguageActivity::class.java)
                            .putExtra(MyLanguageActivity.EXTRA_FROM_SETTINGS, true),
                    )
                }
                SettingRow(Icons.Default.Notifications, stringResource(R.string.notifications), stringResource(R.string.notifications_description))
                SettingRow(Icons.Default.Lock, stringResource(R.string.privacy), stringResource(R.string.privacy_description))
                Button(onClick = { viewModel.clearHistory() }, modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.clear_calculation_history))
                }
            }
            Text(
                stringResource(R.string.app_version),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun SettingRow(icon: ImageVector, title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    Card(modifier = Modifier.fillMaxWidth().noRippleClickable { onClick?.invoke() }, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).background(Color(0xFFE4F5FA), androidx.compose.foundation.shape.CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF18A9D0), modifier = Modifier.size(19.dp))
            }
            Column(Modifier.weight(1f).padding(start = 10.dp)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            if (onClick != null) Icon(Icons.Default.ArrowForward, "Open", tint = MaterialTheme.colorScheme.secondary)
        }
    }
}
