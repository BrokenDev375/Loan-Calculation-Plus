package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.brian.base_application.language.LanguageActivity
import com.loancaculator.core.IapOpener
import com.loancaculator.core.MainActivity
import com.loancaculator.ui.components.findActivity
import com.loancaculator.advertisement.NativeAdSlot

@Composable
fun FinanceSettingsScreen(onNavigate: (String) -> Unit, viewModel: FinanceViewModel) {
    val context = LocalContext.current
    Scaffold(topBar = { FinanceTopBar("Settings", "Preferences and account") }, bottomBar = { FinanceBottomBar("setting", onNavigate) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NativeAdSlot("native_settings", modifier = Modifier.fillMaxWidth(), isSmall = true)
            SettingRow("Premium", "Remove ads and unlock every feature") { IapOpener.open(context, "settings") }
            SettingRow("Exchange base currency", "GBP (British Pound)")
            SettingRow("Language", "System language") { (context.findActivity() as? FragmentActivity)?.let { LanguageActivity.start(it, MainActivity::class.java) } }
            SettingRow("Notifications", "Calculation reminders are enabled")
            SettingRow("Privacy", "Your calculations stay on this device")
            Button(onClick = { viewModel.clearHistory() }, modifier = Modifier.fillMaxWidth()) { Text("Clear calculation history") }
            Text("Loan Calculation Plus\nVersion 1.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SettingRow(title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    Card(onClick = { onClick?.invoke() }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
            if (onClick != null) Icon(Icons.Default.ArrowForward, "Open", tint = MaterialTheme.colorScheme.secondary)
        }
    }
}
