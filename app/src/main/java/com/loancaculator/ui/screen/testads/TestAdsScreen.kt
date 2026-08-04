package com.loancaculator.ui.screen.testads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.loancaculator.firebase.Remote
import com.loancaculator.ui.components.AppScreen
import com.google.android.gms.ads.MobileAds

@Composable
fun TestAdsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val remote = Remote.instance

    val placements = listOf(
        // Placements dùng bởi base-application.
        "open_splash", "inter_splash", "splash_uninstall", "open_all",
        "native_language", "native_keep_user", "native_survey_user", "native_exit_app",
        // Placements của Loan Calculator.
        "native_intro1", "native_intro2", "native_intro3", "native_intro4", "native_permission",
        "native_home", "native_calculator", "native_tools", "native_compare", "native_compare_detail",
        "native_settings", "inter_home", "native_inter_home",
    )

    AppScreen(title = "Test Ads", onBack = onBack) { m ->
        Column(m.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            Button(
                onClick = { runCatching { MobileAds.openAdInspector(context) {} } },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Open Ad Inspector") }

            Text("Ad units (from ads_config)", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            placements.forEach { p ->
                Text(
                    "$p = ${remote.adUnit(p).ifBlank { "(empty)" }}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }
    }
}
