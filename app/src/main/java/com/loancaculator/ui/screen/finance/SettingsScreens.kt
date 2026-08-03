package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.loancaculator.core.IapOpener
import com.loancaculator.R
import com.loancaculator.ui.components.findActivity
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.ui.screen.language.MyLanguageActivity

@Composable
fun FinanceSettingsScreen(onNavigate: (String) -> Unit, viewModel: FinanceViewModel) {
    val context = LocalContext.current
    Scaffold(topBar = { FinanceTopBar(stringResource(R.string.settings), stringResource(R.string.preferences_account), compact = true) }, bottomBar = { FinanceBottomBar("setting", onNavigate) }) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NativeAdSlot("native_settings", modifier = Modifier.fillMaxWidth(), isSmall = true)
            SettingRow(stringResource(R.string.premium), stringResource(R.string.premium_description)) { IapOpener.open(context, "settings") }
            SettingRow(stringResource(R.string.exchange_base_currency), "GBP (${stringResource(R.string.currency_gbp)})")
            SettingRow(stringResource(R.string.language), stringResource(R.string.system_language)) {
                context.findActivity()?.startActivity(
                    android.content.Intent(context, MyLanguageActivity::class.java)
                        .putExtra(MyLanguageActivity.EXTRA_FROM_SETTINGS, true),
                )
            }
            SettingRow(stringResource(R.string.notifications), stringResource(R.string.notifications_description))
            SettingRow(stringResource(R.string.privacy), stringResource(R.string.privacy_description))
            Button(onClick = { viewModel.clearHistory() }, modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 10.dp)) { Text(stringResource(R.string.clear_calculation_history), textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 2) }
            Text(stringResource(R.string.app_version), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    Card(onClick = { onClick?.invoke() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            if (onClick != null) Icon(Icons.Default.ArrowForward, stringResource(R.string.open), tint = MaterialTheme.colorScheme.secondary)
        }
    }
}
