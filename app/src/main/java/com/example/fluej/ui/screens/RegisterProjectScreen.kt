package com.example.fluej.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.Member
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.RegisterProjectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterProjectScreen(
    onBack: () -> Unit,
    onNavigateBottomBar: (String) -> Unit,
    viewModel: RegisterProjectViewModel = viewModel()
) {
    var mostrarSeletor by remember { mutableStateOf(false) }

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
        bottomBar = {
            BottomNavBar(
                selectedTab = Screen.RegisterProject.route,
                onNavigate = onNavigateBottomBar
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "REGISTRAR PROJETO",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )

            CustomTextField(
                label = "Nome do projeto *",
                value = viewModel.nome,
                onValueChange = { viewModel.nome = it },
                placeholder = "Informe o nome do projeto"
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Selecione os membros *",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = viewModel.nomesSelecionados,
                        onValueChange = {},
                        readOnly = true,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Selecione o membro", color = TextSecondary) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Selecionar membros",
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
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { mostrarSeletor = true }
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Descrição",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = viewModel.descricao,
                    onValueChange = { viewModel.descricao = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    maxLines = 8,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Ex: Projeto xxx", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = GrayBorder,
                        unfocusedBorderColor = GrayBorder
                    )
                )
            }

            CustomTextField(
                label = "Total de pontos *",
                value = viewModel.totalPontos,
                onValueChange = { input -> viewModel.totalPontos = input.filter { it.isDigit() }.take(6) },
                placeholder = "0",
                keyboardType = KeyboardType.Number
            )

            viewModel.erro?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.salvar(onBack) },
                enabled = !viewModel.salvando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Text("Salvar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarSeletor) {
        MembrosSeletorSheet(
            membros = viewModel.membrosDoSetor,
            selecionadosIniciais = viewModel.selecionados,
            onConfirm = {
                viewModel.definirSelecionados(it)
                mostrarSeletor = false
            },
            onDismiss = { mostrarSeletor = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MembrosSeletorSheet(
    membros: List<Member>,
    selecionadosIniciais: Set<String>,
    onConfirm: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var rascunho by remember { mutableStateOf(selecionadosIniciais) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Selecionar membros", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text("${rascunho.size} selecionado(s)", fontSize = 12.sp, color = TextSecondary)
                }
                if (membros.isNotEmpty()) {
                    val todos = rascunho.size == membros.size
                    TextButton(
                        onClick = {
                            rascunho = if (todos) emptySet() else membros.map { it.id }.toSet()
                        }
                    ) {
                        Text(if (todos) "Limpar" else "Selecionar todos", color = PrimaryDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (membros.isEmpty()) {
                Text(
                    "Não há membros cadastrados neste setor.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(membros, key = { it.id }) { membro ->
                        val selecionado = membro.id in rascunho
                        MembroAvatar(
                            nome = membro.nome,
                            selecionado = selecionado,
                            onClick = {
                                rascunho = if (selecionado) rascunho - membro.id else rascunho + membro.id
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onConfirm(rascunho) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MembroAvatar(nome: String, selecionado: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(68.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(if (selecionado) PrimaryDark else GrayHeaderTable)
                    .border(2.dp, if (selecionado) PrimaryDark else GrayBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciais(nome),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (selecionado) Color.White else TextPrimary
                )
            }
            if (selecionado) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, PrimaryDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selecionado",
                        tint = PrimaryDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = nome,
            fontSize = 12.sp,
            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = TextPrimary
        )
    }
}

private fun iniciais(nome: String): String {
    val partes = nome.trim().split(" ").filter { it.isNotBlank() }
    return when {
        partes.isEmpty() -> "?"
        partes.size == 1 -> partes[0].take(1).uppercase()
        else -> (partes.first().take(1) + partes.last().take(1)).uppercase()
    }
}