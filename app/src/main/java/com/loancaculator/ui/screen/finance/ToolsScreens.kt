package com.loancaculator.ui.screen.finance

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.loancaculator.advertisement.NativeAdSlot
import com.loancaculator.R
import com.loancaculator.ui.components.dismissKeyboardOnTap
import java.util.Locale
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.roundToInt

private data class ToolDefinition(
    val key: String,
    @androidx.annotation.StringRes val titleRes: Int,
    @androidx.annotation.StringRes val descriptionRes: Int,
    val tint: Color,
)

private val tools = listOf(
    ToolDefinition("currency", R.string.exchange_rate, R.string.tool_currency_desc, Color(0xFFE0F2FE)),
    ToolDefinition("temperature", R.string.temperature, R.string.tool_temperature_desc, Color(0xFFFCE7F3)),
    ToolDefinition("mass", R.string.mass_convert, R.string.tool_mass_desc, Color(0xFFE0F2FE)),
    ToolDefinition("speed", R.string.speed_convert, R.string.tool_speed_desc, Color(0xFFFFEDD5)),
    ToolDefinition("length", R.string.length_convert, R.string.tool_length_desc, Color(0xFFDCFCE7)),
    ToolDefinition("clock", R.string.world_clock, R.string.tool_clock_desc, Color(0xFFFEF3C7)),
)

@Composable
fun ToolsScreen(onNavigate: (String) -> Unit, onConverter: (String) -> Unit, onWorldClock: () -> Unit) {
    Scaffold(topBar = { FinanceTopBar(stringResource(R.string.tools), compact = true) }, bottomBar = { FinanceBottomBar("tools", onNavigate) }) { padding ->
        LazyColumn(Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            item {
                NativeAdSlot(
                    "native_tools",
                    modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().height(100.dp),
                    isSmall = true,
                )
            }
            item {
                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tools.chunked(2).forEach { row ->
                        Row(
                            Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            row.forEach { tool ->
                                ToolTile(
                                    tool = tool,
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                ) { if (tool.key == "clock") onWorldClock() else onConverter(tool.key) }
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolTile(tool: ToolDefinition, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 142.dp)
            .noRippleClickable( onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.SpaceBetween) {
            ToolSpriteIcon(tool.key)
            Column {
                Text(stringResource(tool.titleRes), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                Text(stringResource(tool.descriptionRes), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
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

private data class ToolConverterConfig(
    @androidx.annotation.StringRes val titleRes: Int,
    val units: List<ToolUnit>,
    val defaultFrom: String,
    val defaultTo: String,
    val defaultValue: String = "",
)

private val converterConfigs = mapOf(
    "temperature" to ToolConverterConfig(
        titleRes = R.string.temperature,
        units = listOf(
            ToolUnit("C", "Celsius", "\u00B0C", "\uD83C\uDF21\uFE0F", labelRes = R.string.unit_celsius),
            ToolUnit("F", "Fahrenheit", "\u00B0F", "\uD83C\uDF21\uFE0F", labelRes = R.string.unit_fahrenheit),
            ToolUnit("K", "Kelvin", "K", "\uD83C\uDF21\uFE0F", labelRes = R.string.unit_kelvin),
        ),
        defaultFrom = "C",
        defaultTo = "F",
    ),
    "mass" to ToolConverterConfig(
        titleRes = R.string.mass_convert,
        units = listOf(
            ToolUnit("g", "Gram", "g", "\u2696\uFE0F", 0.001, R.string.unit_gram),
            ToolUnit("kg", "Kilogram", "kg", "\u2696\uFE0F", 1.0, R.string.unit_kilogram),
            ToolUnit("lb", "Pound", "lb", "\u2696\uFE0F", 0.453592, R.string.unit_pound),
            ToolUnit("oz", "Ounce", "oz", "\u2696\uFE0F", 0.0283495, R.string.unit_ounce),
        ),
        defaultFrom = "g",
        defaultTo = "kg",
    ),
    "speed" to ToolConverterConfig(
        titleRes = R.string.speed_convert,
        units = listOf(
            ToolUnit("C", "Speed of light", "C", "\uD83C\uDFCE\uFE0F", 299_792_458.0, R.string.unit_speed_of_light),
            ToolUnit("ma", "Mach", "ma", "\uD83C\uDFCE\uFE0F", 340.29, R.string.unit_mach),
            ToolUnit("kmh", "km/h", "km/h", "\uD83C\uDFCE\uFE0F", 1.0 / 3.6),
            ToolUnit("mph", "mph", "mph", "\uD83C\uDFCE\uFE0F", 0.44704),
        ),
        defaultFrom = "C",
        defaultTo = "ma",
    ),
    "length" to ToolConverterConfig(
        titleRes = R.string.length_convert,
        units = listOf(
            ToolUnit("cm", "Centimeter", "cm", "\uD83D\uDCCF", 0.01, R.string.unit_centimeter),
            ToolUnit("mm", "Millimeter", "mm", "\uD83D\uDCCF", 0.001, R.string.unit_millimeter),
            ToolUnit("m", "Meter", "m", "\uD83D\uDCCF", 1.0, R.string.unit_meter),
            ToolUnit("km", "Kilometer", "km", "\uD83D\uDCCF", 1000.0, R.string.unit_kilometer),
            ToolUnit("in", "Inch", "in", "\uD83D\uDCCF", 0.0254, R.string.unit_inch),
            ToolUnit("ft", "Foot", "ft", "\uD83D\uDCCF", 0.3048, R.string.unit_foot),
        ),
        defaultFrom = "cm",
        defaultTo = "mm",
    ),
)

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun ConverterScreen(kind: String, onBack: () -> Unit) {
    val config = converterConfigs[kind] ?: converterConfigs.getValue("length")
    var fromId by remember(kind) { mutableStateOf(config.defaultFrom) }
    var toId by remember(kind) { mutableStateOf(config.defaultTo) }
    var value by remember(kind) { mutableStateOf(config.defaultValue) }
    val fromUnit = config.units.firstOrNull { it.id == fromId } ?: config.units.first()
    val toUnit = config.units.firstOrNull { it.id == toId } ?: config.units[1]
    val input = value.toDoubleOrNull() ?: 0.0
    val output = convertToolValue(kind, input, fromUnit, toUnit)

    ToolConverterLayout(
        title = stringResource(config.titleRes),
        onBack = onBack,
        fromValue = value,
        onFromValueChange = { value = it },
        toValue = formatToolValue(kind, output),
        fromUnit = fromUnit,
        toUnit = toUnit,
        units = config.units,
        fromNote = toolConversionNote(kind, fromUnit, toUnit),
        toNote = toolConversionNote(kind, toUnit, fromUnit),
        onFromUnitChange = { fromId = it },
        onToUnitChange = { toId = it },
        onSwap = {
            val oldFrom = fromId
            fromId = toId
            toId = oldFrom
        },
        onReset = {
            fromId = config.defaultFrom
            toId = config.defaultTo
            value = config.defaultValue
        },
        onCalculate = { /* The result is kept live as fields change. */ },
    )
}

private fun convertToolValue(kind: String, value: Double, from: ToolUnit, to: ToolUnit): Double {
    if (kind == "temperature") {
        val celsius = when (from.id) {
            "F" -> (value - 32.0) * 5.0 / 9.0
            "K" -> value - 273.15
            else -> value
        }
        return when (to.id) {
            "F" -> celsius * 9.0 / 5.0 + 32.0
            "K" -> celsius + 273.15
            else -> celsius
        }
    }
    return value * (from.toBase ?: 1.0) / (to.toBase ?: 1.0)
}

private fun toolConversionNote(kind: String, from: ToolUnit, to: ToolUnit): String {
    val converted = convertToolValue(kind, 1.0, from, to)
    return "1 ${from.symbol} = ${formatNoteValue(converted)} ${to.symbol}"
}

private fun formatToolValue(kind: String, value: Double): String {
    if (!value.isFinite()) return "0"
    return if (kind == "temperature") {
        String.format(Locale.US, "%.2f", value)
    } else {
        formatNoteValue(value)
    }
}

private fun formatNoteValue(value: Double): String {
    if (value == 0.0) return "0"
    val decimals = when {
        abs(value) >= 1000.0 -> 2
        abs(value) >= 1.0 -> 4
        else -> 6
    }
    return String.format(Locale.US, "%.${decimals}f", value).trimEnd('0').trimEnd('.')
}

private data class WorldCity(val name: String, val zoneId: String)

private val worldCities = listOf(
    WorldCity("ACT", "Australia/Sydney"),
    WorldCity("Abidjan", "Africa/Abidjan"),
    WorldCity("Accra", "Africa/Accra"),
    WorldCity("Acre", "America/Rio_Branco"),
    WorldCity("Adak", "America/Adak"),
    WorldCity("Addis Ababa", "Africa/Addis_Ababa"),
    WorldCity("Adelaide", "Australia/Adelaide"),
    WorldCity("Aden", "Asia/Aden"),
    WorldCity("Bangkok", "Asia/Bangkok"),
    WorldCity("Beijing", "Asia/Shanghai"),
    WorldCity("Berlin", "Europe/Berlin"),
    WorldCity("Cairo", "Africa/Cairo"),
    WorldCity("Chicago", "America/Chicago"),
    WorldCity("Dubai", "Asia/Dubai"),
    WorldCity("Hanoi", "Asia/Ho_Chi_Minh"),
    WorldCity("Hong Kong", "Asia/Hong_Kong"),
    WorldCity("London", "Europe/London"),
    WorldCity("Los Angeles", "America/Los_Angeles"),
    WorldCity("Madrid", "Europe/Madrid"),
    WorldCity("Moscow", "Europe/Moscow"),
    WorldCity("New York", "America/New_York"),
    WorldCity("Paris", "Europe/Paris"),
    WorldCity("Rome", "Europe/Rome"),
    WorldCity("Seoul", "Asia/Seoul"),
    WorldCity("Singapore", "Asia/Singapore"),
    WorldCity("Sydney", "Australia/Sydney"),
    WorldCity("Tokyo", "Asia/Tokyo"),
    WorldCity("Toronto", "America/Toronto"),
)

private val financeBackground = Color(0xFFD0EFFF)

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun WorldClockScreen(onBack: () -> Unit, onAdd: () -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    val clocks by viewModel.clocks.collectAsState()
    Scaffold(
        topBar = { FinanceTopBar(stringResource(R.string.world_clock), onBack = onBack, compact = true) },
        bottomBar = { AddClockButton(onClick = onAdd) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().background(financeBackground).padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                NativeAdSlot(
                    placement = "native_tools",
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    isSmall = false,
                )
            }
            if (clocks.isEmpty()) {
                item { ClockEmptyState() }
            } else {
                clocks.forEach { entry ->
                    item(key = entry.id) { ClockCard(entry.city, entry.zoneId) }
                }
            }
        }
    }
}

@Composable
private fun ClockEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier.size(140.dp).background(Color(0xFFE1EFF5), androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center,
        ) { Text("\uD83D\uDCE6", fontSize = 58.sp) }
        Text(stringResource(R.string.empty_clocks_title), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10233A))
        Text(
            stringResource(R.string.empty_clocks_description),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5B7186),
        )
    }
}

@Composable
private fun ClockCard(city: String, zoneId: String) {
    val zone = ZoneId.of(zoneId)
    val now = ZonedDateTime.now(zone)
    val time = now.format(DateTimeFormatter.ofPattern("HH:mm"))
    val offset = zone.rules.getOffset(now.toInstant()).id.let { if (it == "Z") "+00:00" else it }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(city, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10233A))
                Text(stringResource(R.string.today_utc, offset), style = MaterialTheme.typography.bodySmall, color = Color(0xFF5B7186))
            }
            Text(time, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF10233A))
        }
    }
}

@Composable
private fun AddClockButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().background(financeBackground).padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 12.dp),
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = RoundedCornerShape(28.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF00A6CE)),
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.add_clock), fontWeight = FontWeight.Bold)
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun AddClockScreen(onBack: () -> Unit, viewModel: FinanceViewModel = hiltViewModel()) {
    var query by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf<WorldCity?>(null) }
    val filteredCities = remember(query) {
        worldCities.filter { it.name.contains(query.trim(), ignoreCase = true) }
    }
    val groupedCities = filteredCities.groupBy { it.name.first().uppercaseChar() }.toSortedMap()

    Scaffold(
        modifier = Modifier.dismissKeyboardOnTap(),
        topBar = {
            FinanceTopBar(
                stringResource(R.string.world_clock),
                onBack = onBack,
                compact = true,
                actions = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color.White)
                            .noRippleClickable(enabled = selectedCity != null) {
                            selectedCity?.let { city ->
                                viewModel.addClock(city.name, city.zoneId)
                                onBack()
                            }
                        },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = stringResource(R.string.confirm_city),
                            tint = Color(0xFF18A9D0).copy(alpha = if (selectedCity != null) 1f else .38f),
                            modifier = Modifier.size(18.dp),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(financeBackground)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            CitySearchField(query = query, onQueryChange = { query = it })
            NativeAdSlot(
                placement = "native_tools",
                modifier = Modifier.fillMaxWidth().height(240.dp),
                isSmall = false,
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    if (groupedCities.isEmpty()) {
                        Text(stringResource(R.string.no_cities_found), modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp), textAlign = TextAlign.Center, color = Color(0xFF5B7186))
                    } else {
                        groupedCities.forEach { (letter, cities) ->
                            Text(letter.toString(), modifier = Modifier.padding(top = 2.dp, bottom = 4.dp), color = Color(0xFF00A6CE), fontWeight = FontWeight.Bold)
                            cities.forEach { city ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (selectedCity == city) Color(0xFFE5F7FB) else Color.Transparent, RoundedCornerShape(10.dp))
                                        .clickable { selectedCity = city }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(city.name, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF10233A), fontWeight = FontWeight.SemiBold)
                                    if (selectedCity == city) {
                                        Spacer(Modifier.weight(1f))
                                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.selected), tint = Color(0xFF00A6CE))
                                    }
                                }
                                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFEBF3F6)))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CitySearchField(query: String, onQueryChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) {
                if (query.isEmpty()) Text(stringResource(R.string.search), color = Color(0xFF8BA8B8), style = MaterialTheme.typography.bodyLarge)
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF10233A)),
                )
            }
            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search), tint = Color(0xFF00A6CE))
        }
    }
}
