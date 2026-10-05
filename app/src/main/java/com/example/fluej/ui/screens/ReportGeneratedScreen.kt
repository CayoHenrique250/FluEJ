package com.example.fluej.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportGeneratedScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = SurfaceLight,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FluEJ", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gestão de membros", fontSize = 14.sp, color = TextSecondary)
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(selectedTab = "Relatórios") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .border(2.dp, PrimaryDark, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sucesso",
                    tint = PrimaryDark,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "Relatório gerado com sucesso!",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GrayBorder, RoundedCornerShape(8.dp))
                    .background(CardBackground)
                    .horizontalScroll(rememberScrollState())
            ) {
                Column {
                
                    Row(
                        modifier = Modifier
                            .background(GrayHeaderTable)
                            .padding(vertical = 10.dp)
                    ) {
                        listOf("Nome", "Cargo", "Pontos Jan/2026", "Pontos Fev/2026", "Total de pontos", "Total de horas").forEach { header ->
                            Text(
                                text = header,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(110.dp),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = TextPrimary
                            )
                        }
                    }

                    HorizontalDivider(color = GrayBorder)

                    val sampleData = listOf(
                        listOf("Aninha Clarinha", "Gestor(a) de RH", "10", "10", "10", "3h"),
                        listOf("Duda Vlogs", "Gestor(a) Marketing", "50", "50", "50", "15h"),
                        listOf("Marciano", "Membro de projetos", "20", "20", "20", "6h")
                    )

                    sampleData.forEach { row ->
                        Row(modifier = Modifier.padding(vertical = 12.dp)) {
                            row.forEach { cell ->
                                Text(
                                    text = cell,
                                    modifier = Modifier.width(110.dp),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    color = TextPrimary
                                )
                            }
                        }
                        HorizontalDivider(color = GrayBorder)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Baixar",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Baixar Excel", color = Color.White, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryDark)
            ) {
                Text("Voltar", color = PrimaryDark, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}