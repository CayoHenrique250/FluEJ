package com.example.fluej.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavBar(
    selectedTab: String = "Início",
    onHomeClick: () -> Unit = {},
    onMembersClick: () -> Unit = {},
    onPointsClick: () -> Unit = {},
    onReportsClick: () -> Unit = {}
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == "Início",
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Início"
                )
            },
            label = { Text("Início") }
        )

        NavigationBarItem(
            selected = selectedTab == "Membros",
            onClick = onMembersClick,
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Membros"
                )
            },
            label = { Text("Membros") }
        )

        NavigationBarItem(
            selected = selectedTab == "Pontos",
            onClick = onPointsClick,
            icon = {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Pontos"
                )
            },
            label = { Text("Pontos") }
        )

        NavigationBarItem(
            selected = selectedTab == "Relatórios",
            onClick = onReportsClick,
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