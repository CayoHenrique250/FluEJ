package com.example.fluej.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import com.example.fluej.navigation.Screen

@Composable
fun BottomNavBar(
    selectedTab: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {

        NavigationBarItem(
            selected = selectedTab == Screen.Home.route,
            onClick = { onNavigate(Screen.Home.route) },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Início"
                )
            },
            label = { Text("Início") }
        )

        NavigationBarItem(
            selected = selectedTab == Screen.Members.route,
            onClick = { onNavigate(Screen.Members.route) },
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Membros"
                )
            },
            label = { Text("Membros") }
        )

        NavigationBarItem(
            selected = selectedTab == Screen.Pontos.route,
            onClick = { onNavigate(Screen.Pontos.route) },
            icon = {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Pontos"
                )
            },
            label = { Text("Pontos") }
        )

        NavigationBarItem(
            selected = selectedTab == Screen.Reports.route,
            onClick = { onNavigate(Screen.Reports.route) },
            icon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Relatórios"
                )
            },
            label = { Text("Relatórios") }
        )
    }
}