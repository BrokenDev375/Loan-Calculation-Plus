package com.loancaculator.ui.screen.finance

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.data.finance.CalculatorType
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private data class ToolDefinition(val key: String, val title: String, val description: String, val tint: Color)

private val tools = listOf(
    ToolDefinition("currency", "Exchange Rate", "Easily convert between currencies", Color(0xFFE0F2FE)),
    ToolDefinition("temperature", "Temperature", "Celsius, Fahrenheit and Kelvin", Color(0xFFFCE7F3)),
    ToolDefinition("mass", "Mass Convert", "Grams, pounds and ounces", Color(0xFFE0F2FE)),
    ToolDefinition("speed", "Speed Convert", "Kilometers and miles per hour", Color(0xFFFFEDD5)),
    ToolDefinition("length", "Length Convert", "Inches, meters and more", Color(0xFFDCFCE7)),
    ToolDefinition("clock", "World Clock", "Track time in saved cities", Color(0xFFFEF3C7)),
)

@Composable
fun ToolsScreen(onNavigate: (String) -> Unit, onOpenCalculator: (CalculatorType) -> Unit, onConverter: (String) -> Unit, onWorldClock: () -> Unit) {
    Scaffold(topBar = { FinanceTopBar("Tools", "Converters and utilities", compact = true) }, bottomBar = { FinanceBottomBar("tools", onNavigate) }) { padding ->
        LazyColumn(Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            item { NativeAdSlot("native_tools", modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), isSmall = true) }
            item {
                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tools.chunked(2).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { tool -> Box(Modifier.weight(1f)) { ToolTile(tool) { if (tool.key == "clock") onWorldClock() else onConverter(tool.key) } } }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                Card(onClick = { onOpenCalculator(CalculatorType.PERSONAL) }, modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Build, null, tint = MaterialTheme.colorScheme.secondary); Column(Modifier.weight(1f).padding(start = 12.dp)) { Text("Quick calculator", style = MaterialTheme.typography.titleMedium); Text("Jump straight to a loan estimate", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                }
            }
        }
    }
}

@Composable
private fun ToolTile(tool: ToolDefinition, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().height(142.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            ToolSpriteIcon(tool.key)
            Column { Text(tool.title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary); Text(tool.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
private fun ToolSpriteIcon(key: String) {
    val index = when (key) { "currency" -> 0; "temperature" -> 1; "mass" -> 2; "speed" -> 3; "length" -> 4; else -> 5 }
    val context = LocalContext.current
    val sprite = remember {
        BitmapFactory.decodeResource(context.resources, com.loancaculator.R.drawable.finance_tools).asImageBitmap()
    }
    val sourceSize = IntSize(sprite.width / 2, sprite.height / 3)
    val sourceOffset = IntOffset(
        x = sourceSize.width * (index % 2),
        y = sourceSize.height * (index / 2)
    )
    Box(Modifier.size(54.dp).clip(RoundedCornerShape(14.dp))) {
        Canvas(Modifier.fillMaxWidth().height(54.dp)) {
            drawImage(
                image = sprite,
                srcOffset = sourceOffset,
                srcSize = sourceSize,
                dstSize = IntSize(size.width.roundToInt(), size.height.roundToInt())
            )
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ConverterScreen(kind: String, onBack: () -> Unit) {
    var value by remember { mutableStateOf("1") }
    var result by remember { mutableStateOf("-") }
    val title = when (kind) { "currency" -> "Exchange Rate"; "temperature" -> "Temperature"; "mass" -> "Mass Convert"; "speed" -> "Speed Convert"; "length" -> "Length Convert"; else -> "Unit Converter" }
    val hint = when (kind) { "currency" -> "Offline reference rate: 1 USD = 25,000 VND"; "temperature" -> "Convert Celsius to Fahrenheit"; "mass" -> "Convert kilograms to pounds"; "speed" -> "Convert km/h to mph"; else -> "Convert meters to feet" }
    Scaffold(topBar = { FinanceTopBar(title, hint, onBack, compact = true) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(hint, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Button(onClick = {
                val input = value.toDoubleOrNull() ?: 0.0
                result = when (kind) { "currency" -> "%.2f VND".format(input * 25000); "temperature" -> "%.2f F".format(input * 9 / 5 + 32); "mass" -> "%.2f lb".format(input * 2.20462); "speed" -> "%.2f mph".format(input * 0.621371); else -> "%.2f ft".format(input * 3.28084) }
            }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Build, null); Spacer(Modifier.size(8.dp)); Text("Convert") }
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F6F3))) { Text("Result: $result", Modifier.padding(18.dp), style = MaterialTheme.typography.titleLarge, color = Color(0xFF0B2E4F)) }
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun WorldClockScreen(onBack: () -> Unit, onAdd: () -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val clocks by viewModel.clocks.collectAsState()
    Scaffold(topBar = { FinanceTopBar("World Clock", "Track time across cities", onBack, compact = true, actions = { IconButton(onClick = onAdd) { Icon(Icons.Default.Add, "Add city", tint = Color.White) } }) }) { padding ->
        LazyColumn(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (clocks.isEmpty()) item { Text("Add a city to track its local time.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            clocks.forEach { entry ->
                item {
                    val now = ZonedDateTime.now(ZoneId.of(entry.zoneId)).format(DateTimeFormatter.ofPattern("EEE, dd MMM  HH:mm"))
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(entry.city, style = MaterialTheme.typography.titleMedium); Text(entry.zoneId, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall); Text(now, style = MaterialTheme.typography.headlineSmall) }; IconButton(onClick = { viewModel.removeClock(entry) }) { Icon(Icons.Default.Delete, "Remove") } } }
                }
            }
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun AddClockScreen(onBack: () -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    var city by remember { mutableStateOf("Ho Chi Minh City") }
    var zone by remember { mutableStateOf("Asia/Ho_Chi_Minh") }
    Scaffold(topBar = { FinanceTopBar("Add City", "Save a local time zone", onBack, compact = true) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(city, { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(zone, { zone = it }, label = { Text("Time zone ID") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { viewModel.addClock(city, zone); onBack() }, modifier = Modifier.fillMaxWidth()) { Text("Add city") }
        }
    }
}
