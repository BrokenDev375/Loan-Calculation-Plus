package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loancaculator.R

@Composable
fun FinanceHistoryScreen(
    onBack: () -> Unit,
    onOpen: (Long) -> Unit,
    viewModel: FinanceViewModel = hiltViewModel(),
) {
    val history by viewModel.history.collectAsState()
    var segment by remember { mutableStateOf("Calculator") }
    var selectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    val visible = history.filter { item ->
        if (segment == "Investment") item.category == "Deposits" else item.category == "Loans"
    }

    fun changeSegment(next: String) {
        segment = next
        selectedIds = emptySet()
    }

    fun toggleSelectionMode() {
        selectionMode = !selectionMode
        selectedIds = emptySet()
    }

    Scaffold(
        containerColor = Color(0xFFD0EFFF),
        topBar = {
            FinanceTopBar(
                title = stringResource(R.string.history),
                onBack = onBack,
                compact = true,
                actions = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape)
                            .noRippleClickable { toggleSelectionMode() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (selectionMode) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = stringResource(if (selectionMode) R.string.exit_selection else R.string.select_history),
                            tint = Color(0xFF18A9D0),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
            )
        },
        bottomBar = {
            if (selectionMode && selectedIds.isNotEmpty()) {
                Button(
                    onClick = {
                        selectedIds.forEach { viewModel.deleteHistory(it) }
                        selectedIds = emptySet()
                        selectionMode = false
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).heightIn(min = 52.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC24545)),
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(stringResource(R.string.delete_selected, selectedIds.size))
                }
            }
        },
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = if (selectedIds.isNotEmpty()) 92.dp else 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Row(Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        HistorySegment(stringResource(R.string.calculator), segment == "Calculator") { changeSegment("Calculator") }
                        HistorySegment(stringResource(R.string.investment_history), segment == "Investment") { changeSegment("Investment") }
                    }
                }
            }
            if (visible.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE1E9EF)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                    ) {
                        Box(
                            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 34.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                if (segment == "Investment") stringResource(R.string.no_investment_history) else stringResource(R.string.no_calculator_history),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            } else {
                items(visible, key = { it.id }) { item ->
                    FinanceHistoryCard(
                        item = item,
                        onOpen = { onOpen(item.id) },
                        viewAllStyle = true,
                        selectionMode = selectionMode,
                        selected = item.id in selectedIds,
                        onToggleSelected = {
                            selectedIds = if (item.id in selectedIds) selectedIds - item.id else selectedIds + item.id
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.HistorySegment(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f).heightIn(min = 44.dp),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        colors = if (selected) {
            ButtonDefaults.buttonColors(containerColor = Color(0xFF16B2D7), contentColor = Color.White)
        } else {
            ButtonDefaults.textButtonColors(contentColor = Color(0xFF10233A))
        },
    ) {
        Text(label, textAlign = TextAlign.Center, maxLines = 2)
    }
}
