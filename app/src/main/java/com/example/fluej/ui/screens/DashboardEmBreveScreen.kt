package com.example.fluej.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluej.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardEmBreveScreen(titulo: String, onLogout: () -> Unit) {
    Scaffold(
        containerColor = SurfaceLight,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight),
                title = {
                    Text("FluEJ", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = TextPrimary)
                },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Sair", color = TextPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Olá, $titulo!",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Este dashboard ainda está em construção.",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}