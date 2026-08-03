package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.R
import androidx.compose.ui.res.stringResource

internal data class ToolUnit(
    val id: String,
    val label: String,
    val symbol: String,
    val flag: String,
    val toBase: Double? = null,
    @androidx.annotation.StringRes val labelRes: Int? = null,
)

@Composable
internal fun toolUnitLabel(unit: ToolUnit): String = unit.labelRes?.let { stringResource(it) } ?: unit.label

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
internal fun ToolConverterLayout(
    title: String,
    onBack: () -> Unit,
    fromValue: String,
    onFromValueChange: ((String) -> Unit)?,
    toValue: String,
    fromUnit: ToolUnit,
    toUnit: ToolUnit,
    units: List<ToolUnit>,
    fromNote: String,
    toNote: String,
    onFromUnitChange: (String) -> Unit,
    onToUnitChange: (String) -> Unit,
    onSwap: () -> Unit,
    onReset: () -> Unit,
    onCalculate: () -> Unit,
    calculateEnabled: Boolean = true,
    footer: String? = null,
) {
    Scaffold(
        topBar = { FinanceTopBar(title, onBack = onBack, compact = true) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD0EFFF))
                    .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ConverterActionButton(
                    text = stringResource(R.string.reset_fields),
                    icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    background = Color(0xFFFFBD19),
                    contentColor = Color(0xFF10233A),
                    onClick = onReset,
                )
                ConverterActionButton(
                    text = stringResource(R.string.calculate),
                    icon = { Icon(Icons.Default.Build, contentDescription = null) },
                    background = Color(0xFF00A6CE),
                    contentColor = Color.White,
                    onClick = onCalculate,
                    enabled = calculateEnabled,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .background(Color(0xFFD0EFFF))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NativeAdSlot(
                placement = "native_tools",
                modifier = Modifier.fillMaxWidth().height(240.dp),
                isSmall = false,
            )

            ConverterCard(
                value = fromValue,
                onValueChange = onFromValueChange,
                unit = fromUnit,
                units = units,
                note = fromNote,
                keyboardType = KeyboardType.Decimal,
                readOnly = false,
                onUnitChange = onFromUnitChange,
            )

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                SwapButton(onClick = onSwap)
            }

            ConverterCard(
                value = toValue,
                onValueChange = null,
                unit = toUnit,
                units = units,
                note = toNote,
                keyboardType = KeyboardType.Decimal,
                readOnly = true,
                onUnitChange = onToUnitChange,
            )

            footer?.let {
                Text(
                    text = it,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ConverterCard(
    value: String,
    onValueChange: ((String) -> Unit)?,
    unit: ToolUnit,
    units: List<ToolUnit>,
    note: String,
    keyboardType: KeyboardType,
    readOnly: Boolean,
    onUnitChange: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BasicTextField(
                    value = value,
                    onValueChange = { onValueChange?.invoke(sanitizeDecimal(it)) },
                    modifier = Modifier.weight(1f).padding(vertical = 1.dp),
                    enabled = onValueChange != null,
                    readOnly = readOnly,
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    decorationBox = { innerTextField ->
                        Box {
                            if (value.isEmpty() && !readOnly) {
                                Text(
                                    "0",
                                    style = TextStyle(
                                        color = Color(0xFF9BAEB6),
                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                )
                            }
                            innerTextField()
                        }
                    },
                    textStyle = TextStyle(
                        color = Color(0xFF10233A),
                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Spacer(Modifier.width(12.dp))
                Text(unit.symbol, color = Color(0xFF6A8696), style = MaterialTheme.typography.titleMedium)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE4EEF2)))
            UnitMenu(selected = unit, options = units, onSelected = onUnitChange)
            Text(
                text = note,
                modifier = Modifier.padding(horizontal = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF378092),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun UnitMenu(selected: ToolUnit, options: List<ToolUnit>, onSelected: (String) -> Unit) {
    var expanded by remember(selected.id) { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { expanded = true }.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(32.dp).background(Color(0xFFF0F5F8), CircleShape),
                contentAlignment = Alignment.Center,
            ) { Text(selected.flag, style = MaterialTheme.typography.titleMedium) }
            Spacer(Modifier.width(10.dp))
            Text(toolUnitLabel(selected), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, color = Color(0xFF10233A), fontWeight = FontWeight.Bold, maxLines = 2)
            Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(R.string.currency), tint = Color(0xFF6A8696))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    leadingIcon = { Text(option.flag) },
                    text = { Text(toolUnitLabel(option)) },
                    onClick = { expanded = false; onSelected(option.id) },
                )
            }
        }
    }
}

@Composable
private fun SwapButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(Color(0xFFEAF4F8), CircleShape)
            .padding(3.dp)
            .background(Color(0xFFF2A229), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(44.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp).rotate(90f))
                Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp).rotate(270f))
            }
        }
    }
}

@Composable
private fun RowScope.ConverterActionButton(
    text: String,
    icon: @Composable () -> Unit,
    background: Color,
    contentColor: Color,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.weight(1f).heightIn(min = 52.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = background, contentColor = contentColor),
    ) {
        icon()
        Spacer(Modifier.width(6.dp))
        Text(text, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

internal fun sanitizeDecimal(value: String): String {
    val negative = value.startsWith("-")
    val filtered = value.removePrefix("-").filter { it.isDigit() || it == '.' }
    val dot = filtered.indexOf('.')
    val normalized = if (dot >= 0) filtered.substring(0, dot + 1) + filtered.substring(dot + 1).replace(".", "") else filtered
    return if (negative) "-$normalized" else normalized
}
