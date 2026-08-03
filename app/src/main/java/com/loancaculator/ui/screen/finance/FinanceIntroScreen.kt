package com.loancaculator.ui.screen.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun FinanceIntroScreen(onFinish: () -> Unit) {
    val pages = listOf(
        "Understand every payment" to "Calculate monthly payments, total interest and payoff time.",
        "Plan your savings" to "Compare fixed and recurring deposits with clear maturity values.",
        "Keep your decisions close" to "Save results offline, compare options and share a PDF when ready.",
    )
    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val last = pager.currentPage == pages.lastIndex
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        HorizontalPager(state = pager, modifier = Modifier.fillMaxWidth()) { page ->
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("LC+", fontSize = 64.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1368CE))
                Text(pages[page].first, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(pages[page].second, textAlign = TextAlign.Center, color = Color.Gray)
            }
        }
        Row(Modifier.padding(vertical = 28.dp), horizontalArrangement = Arrangement.Center) {
            pages.indices.forEach { index ->
                Spacer(Modifier.padding(4.dp).size(if (index == pager.currentPage) 10.dp else 8.dp).clip(CircleShape).background(if (index == pager.currentPage) Color(0xFF1368CE) else Color.LightGray))
            }
        }
        Button(onClick = { if (last) onFinish() else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } }, modifier = Modifier.fillMaxWidth()) {
            Text(if (last) "Start calculating" else "Continue")
        }
    }
}
