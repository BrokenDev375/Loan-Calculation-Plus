package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanceHistoryScreen(onBack: () -> Unit, onOpen: (Long) -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val history by viewModel.history.collectAsState()
    var segment by remember { mutableStateOf("Calculation") }
    val visible = history.filter { if (segment == "Investment") it.category == "Deposits" else it.category == "Loans" }

    Scaffold(topBar = { FinanceTopBar("History", "Review your saved calculations", onBack) }) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Row(Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        HistorySegment("Calculation", segment == "Calculation") { segment = "Calculation" }
                        HistorySegment("Investment", segment == "Investment") { segment = "Investment" }
                    }
                }
            }
            if (visible.isEmpty()) {
                item {
                    Text(
                        if (segment == "Investment") "No investment history yet" else "No calculation history yet",
                        Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(visible) { item ->
                Card(
                    onClick = { onOpen(item.id) },
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(Date(item.createdAt)),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            IconButton(onClick = { viewModel.deleteHistory(item.id) }) { Icon(Icons.Default.Delete, "Delete") }
                        }
                        Text(
                            item.resultSummary.replace("|", "  \u2022  "),
                            Modifier.padding(top = 12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.HistorySegment(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f).height(44.dp),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        colors = if (selected) {
            ButtonDefaults.buttonColors(containerColor = Color(0xFF16B2D7), contentColor = Color.White)
        } else {
            ButtonDefaults.textButtonColors(contentColor = Color(0xFF10233A))
        },
    ) {
        Text(label)
    }
}
