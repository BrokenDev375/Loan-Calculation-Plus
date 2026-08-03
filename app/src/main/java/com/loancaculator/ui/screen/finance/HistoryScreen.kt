package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanceHistoryScreen(onBack: () -> Unit, onOpen: (Long) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    var segment by remember { mutableStateOf("Calculator") }
    val visible = history.filter { if (segment == "Investment") it.category == "Deposits" else it.category == "Loans" }
    Scaffold(topBar = { FinanceTopBar("History", "Review your saved calculations", onBack) }) { padding ->
        if (history.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(padding), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { Text("No saved calculations yet", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Calculator", "Investment").forEach { tab ->
                            if (segment == tab) Button(onClick = { segment = tab }, modifier = Modifier.weight(1f)) { Text(tab) }
                            else OutlinedButton(onClick = { segment = tab }, modifier = Modifier.weight(1f)) { Text(tab) }
                        }
                    }
                }
                if (visible.isEmpty()) item { Text("No items in this section", Modifier.padding(20.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                items(visible) { item ->
                    Card(onClick = { onOpen(item.id) }, modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                                    Text(SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date(item.createdAt)), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { viewModel.deleteHistory(item.id) }) { Icon(Icons.Default.Delete, "Delete") }
                            }
                            Text(item.resultSummary.replace("|", "  •  "), Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
