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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.RegisterMemberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterMemberScreen(
    onBack: () -> Unit,
    viewModel: RegisterMemberViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = SurfaceLight,
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("FluEJ", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gestão de membros", fontSize = 14.sp, color = TextSecondary)
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(selectedTab = "Membros") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CADASTRAR MEMBRO",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, GrayBorder, RoundedCornerShape(24.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (selectedTab == 0) PrimaryDark else Color.Transparent)
                        .clickable { selectedTab = 0 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Dados pessoais", color = if (selectedTab == 0) Color.White else TextPrimary)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(if (selectedTab == 1) PrimaryDark else Color.Transparent)
                        .clickable { selectedTab = 1 },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cargo/setor", color = if (selectedTab == 1) Color.White else TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedTab == 0) {
                CustomTextField(label = "Nome completo *", value = viewModel.nome, onValueChange = { viewModel.nome = it })
                CustomTextField(label = "CPF *", value = viewModel.cpf, onValueChange = { viewModel.cpf = it })
                CustomTextField(
                    label = "Data de nascimento *",
                    value = viewModel.dataNascimento,
                    onValueChange = { viewModel.dataNascimento = it },
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = TextSecondary) }
                )
                CustomTextField(label = "E-mail *", value = viewModel.email, onValueChange = { viewModel.email = it })
                CustomTextField(label = "Telefone *", value = viewModel.telefone, onValueChange = { viewModel.telefone = it })

                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = { selectedTab = 1 }, // Vai para a aba Cargo/Setor
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Text("Próximo", color = Color.White, fontWeight = FontWeight.Bold)
                }

            } else {
                CustomTextField(
                    label = "Cargo *",
                    value = viewModel.cargo,
                    onValueChange = { viewModel.cargo = it },
                    placeholder = "Selecione o cargo",
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary) }
                )
                CustomTextField(
                    label = "Setor *",
                    value = viewModel.setor,
                    onValueChange = { viewModel.setor = it },
                    placeholder = "Selecione o setor",
                    trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.saveMember {
                            onBack() 
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text(placeholder, color = TextSecondary) },
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardBackground,
                unfocusedContainerColor = CardBackground,
                focusedBorderColor = GrayBorder,
                unfocusedBorderColor = GrayBorder
            )
        )
    }
}