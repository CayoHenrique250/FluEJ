package com.example.fluej.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fluej.ui.screens.HomeScreen
import com.example.fluej.ui.screens.RegisterMemberScreen
import com.example.fluej.ui.screens.ReportGeneratedScreen
import com.example.fluej.ui.screens.ReportsScreen
import com.example.fluej.ui.membros.TelaMembrosScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object RegisterMember : Screen("register_member")
    object Reports : Screen("reports")
    object ReportGenerated : Screen("report_generated")
    object Members : Screen("members")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
       composable(Screen.Home.route) {
    HomeScreen(
        onNavigateToRegister = {
            navController.navigate(Screen.RegisterMember.route)
        },
        onNavigateToMembers = {
            navController.navigate(Screen.Members.route)
        }
    )
}

        composable(Screen.RegisterMember.route) {
            RegisterMemberScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Reports.route) {
            ReportsScreen(
                onGenerateReport = {
                    navController.navigate(Screen.ReportGenerated.route)
                }
            )
        }

        composable(Screen.ReportGenerated.route) {
            ReportGeneratedScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Members.route) {
            TelaMembrosScreen(
                onMembroClick = { membro ->
                    // Futuramente, abrir os detalhes do membro.
                }
            )
        }
    }
}