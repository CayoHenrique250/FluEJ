package com.example.fluej.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onGenerateReport: () -> Unit) {
    var selectedPeriod by remember { mutableStateOf("Semanal") }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
         
            Text("Período", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf("Semanal", "Mensal", "Anual").forEach { period ->
                    val isSelected = selectedPeriod == period
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryDark else UnselectedChipBg)
                            .border(1.dp, if (isSelected) PrimaryDark else GrayBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedPeriod = period }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = period,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            OutlinedTextField(
                value = "08/07/2026 - 14/07/2026",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Calendário",
                        tint = TextSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = GrayBorder,
                    unfocusedBorderColor = GrayBorder
                )
            )

            Text("Filtros", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

            OutlinedTextField(
                value = "Todos os cargos",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Selecionar Cargo",
                        tint = TextSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = GrayBorder,
                    unfocusedBorderColor = GrayBorder
                )
            )

            OutlinedTextField(
                value = "Todos os membros",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Selecionar Membro",
                        tint = TextSecondary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardBackground,
                    unfocusedContainerColor = CardBackground,
                    focusedBorderColor = GrayBorder,
                    unfocusedBorderColor = GrayBorder
                )
            )

            Button(
                onClick = onGenerateReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Text("Gerar relatório", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Text("Resumo do período", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GrayBorder, RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text("2.450", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Total de pontos", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GrayBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GrayBorder, RoundedCornerShape(8.dp))
                        )
                        Column {
                            Text("245h", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Total de horas", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

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
                    contentDescription = "Exportar",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Exportar para Excel", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}