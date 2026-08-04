package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loancaculator.R
import com.loancaculator.ui.components.dismissKeyboardOnTap

@Composable
fun CurrencyScreen(
    initialCode: String,
    onBack: () -> Unit,
    onDone: (String) -> Unit,
) {
    var selectedCode by remember(initialCode) { mutableStateOf(initialCode) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredCurrencies = remember(searchQuery) {
        loanCurrencies.filter { currency ->
            searchQuery.isBlank() ||
                currency.code.contains(searchQuery.trim(), ignoreCase = true) ||
                currency.name.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    Scaffold(
        modifier = Modifier.dismissKeyboardOnTap(),
        containerColor = Color(0xFFF7FBFC),
        topBar = {
            FinanceTopBar(
                title = stringResource(R.string.exchange_base_currency),
                onBack = onBack,
                compact = true,
                actions = {
                    Button(
                        onClick = { onDone(selectedCode) },
                        modifier = Modifier.heightIn(min = 36.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF16B2D7),
                        ),
                    ) {
                        Text(stringResource(R.string.language_done), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Text(
                stringResource(R.string.currency_description),
                fontSize = 14.sp,
                color = Color(0xFF6E6B7B),
            )
            Spacer(Modifier.size(14.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(stringResource(R.string.currency_search_hint), color = Color(0xFF9E9E9E), fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF16B2D7),
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                ),
            )
            Spacer(Modifier.size(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(filteredCurrencies, key = { it.code }) { currency ->
                    val isSelected = currency.code.equals(selectedCode, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF16B2D7) else Color(0xFFF3F4F6),
                                RoundedCornerShape(16.dp),
                            )
                            .background(if (isSelected) Color(0xFFE8F8FC) else Color.White)
                            .noRippleClickable { selectedCode = currency.code }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(42.dp).background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(currency.flag, fontSize = 25.sp)
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(currencyName(currency.code), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                            Spacer(Modifier.size(2.dp))
                            Text("${currency.code}  •  ${currency.symbol}", fontSize = 13.sp, color = Color(0xFF8B95A1))
                        }
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedCode = currency.code },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF16B2D7),
                                unselectedColor = Color(0xFFD1D5DB),
                            ),
                        )
                    }
                }
            }
        }
    }
}
