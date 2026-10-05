package com.example.fluej.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fluej.ui.screens.ReportGeneratedScreen
import com.example.fluej.ui.screens.ReportsScreen

sealed class Screen(val route: String) {
    object Reports : Screen("reports")
    object ReportGenerated : Screen("report_generated")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Reports.route
    ) {
        composable(Screen.Reports.route) {
            ReportsScreen(
                onGenerateReport = {
                    navController.navigate(Screen.ReportGenerated.route)
                }
            )
        }
        composable(Screen.ReportGenerated.route) {
            ReportGeneratedScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}