package com.loancaculator.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.loancaculator.core.AppStorage
import com.loancaculator.core.PdfShare
import com.loancaculator.data.finance.CalculatorType
import com.loancaculator.ui.screen.finance.AddClockScreen
import com.loancaculator.ui.screen.finance.CalculatorScreen
import com.loancaculator.ui.screen.finance.CompareDetailScreen
import com.loancaculator.ui.screen.finance.CompareScreen
import com.loancaculator.ui.screen.finance.ConverterScreen
import com.loancaculator.ui.screen.finance.ApiConverterScreen
import com.loancaculator.ui.screen.finance.FinanceHomeScreen
import com.loancaculator.ui.screen.finance.FinanceSettingsScreen
import com.loancaculator.ui.screen.finance.FinanceViewModel
import com.loancaculator.ui.screen.finance.FinanceHistoryScreen
import com.loancaculator.ui.screen.finance.ResultScreen
import com.loancaculator.ui.screen.finance.ToolsScreen
import com.loancaculator.ui.screen.finance.WorldClockScreen
import com.loancaculator.ui.screen.finance.FinanceIntroScreen

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun AppNavHost(startRoute: String, navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val back: () -> Unit = { navController.popBackStack() }
    val toHome = {
        AppStorage.setOnboardingDone(context)
        navController.navigate(Screen.Home.route) {
            popUpTo(0) { inclusive = true }
        }
    }
    fun openCalculator(type: CalculatorType) = navController.navigate(Screen.calculator(type.key))
    fun navigateTab(route: String) {
        navController.navigate(route) {
            popUpTo(Screen.Home.route) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(navController = navController, startDestination = startRoute) {
        composable(Screen.Intro.route) { FinanceIntroScreen(onFinish = { toHome() }) }
        composable(Screen.Home.route) {
            FinanceHomeScreen(onNavigate = ::navigateTab, onOpen = ::openCalculator)
        }
        composable(Screen.Uninstall.route) {
            FinanceHomeScreen(onNavigate = ::navigateTab, onOpen = ::openCalculator)
        }
        composable(Screen.Tools.route) {
            ToolsScreen(onNavigate = ::navigateTab, onOpenCalculator = ::openCalculator,
                onConverter = { navController.navigate(Screen.converter(it)) }, onWorldClock = { navController.navigate(Screen.WorldClock.route) })
        }
        composable(Screen.Compare.route) {
            CompareScreen(onNavigate = ::navigateTab, onOpen = { navController.navigate(Screen.result(it)) })
        }
        composable(Screen.Setting.route) {
            FinanceSettingsScreen(onNavigate = ::navigateTab, viewModel = hiltViewModel())
        }
        composable("${Screen.Calculator.route}/{${Screen.ARG_TYPE}}", arguments = listOf(navArgument(Screen.ARG_TYPE) { type = NavType.StringType })) { entry ->
            val type = CalculatorType.fromKey(entry.arguments?.getString(Screen.ARG_TYPE).orEmpty())
            CalculatorScreen(type = type, onBack = back, onSaved = { navController.navigate(Screen.result(it)) })
        }
        composable("${Screen.Result.route}/{${Screen.ARG_ID}}", arguments = listOf(navArgument(Screen.ARG_ID) { type = NavType.LongType })) { entry ->
            val id = entry.arguments?.getLong(Screen.ARG_ID) ?: 0L
            val viewModel: FinanceViewModel = hiltViewModel()
            val context = LocalContext.current
            ResultScreen(id = id, onBack = back, onCompare = { viewModel.addCompare(id); navController.navigate(Screen.Compare.route) }, onShare = { title, summary -> PdfShare.shareResult(context, title, summary) })
        }
        composable(Screen.History.route) { FinanceHistoryScreen(onBack = back, onOpen = { navController.navigate(Screen.result(it)) }) }
        composable("${Screen.Converter.route}/{${Screen.ARG_TYPE}}", arguments = listOf(navArgument(Screen.ARG_TYPE) { type = NavType.StringType })) { entry ->
            val kind = entry.arguments?.getString(Screen.ARG_TYPE).orEmpty()
            if (kind == "currency") ApiConverterScreen(back) else ConverterScreen(kind, back)
        }
        composable(Screen.WorldClock.route) { WorldClockScreen(onBack = back, onAdd = { navController.navigate(Screen.AddClock.route) }) }
        composable(Screen.AddClock.route) { AddClockScreen(onBack = back) }
        composable("${Screen.HistoryDetail.route}/{${Screen.ARG_ID}}", arguments = listOf(navArgument(Screen.ARG_ID) { type = NavType.LongType })) { entry ->
            CompareDetailScreen(entry.arguments?.getLong(Screen.ARG_ID) ?: 0L, back)
        }
    }
}
