package com.example.fluej.ui.membros

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.Options
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.viewmodels.MembrosViewModel

@Composable
fun TelaMembrosScreen(
    onMembroClick: (Membro) -> Unit = {},
    onNavigateBottomBar: (String) -> Unit,
    viewModel: MembrosViewModel = viewModel()
) {
    var busca by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }
    var membroSelecionado by remember { mutableStateOf<Membro?>(null) }

    LaunchedEffect(Unit) { viewModel.atualizar() }

    val filtros = buildList {
        add("Todos")
        add("Ativos")
        add("Inativos")
        if (!viewModel.escopoPorSetor) addAll(Options.setores)
    }

    val membrosFiltrados = viewModel.membros.filter { membro ->
        val correspondeBusca =
            membro.nome.contains(busca, ignoreCase = true) ||
                    membro.cargo.contains(busca, ignoreCase = true)

        val correspondeFiltro = when (filtro) {
            "Todos" -> true
            "Ativos" -> membro.ativo
            "Inativos" -> !membro.ativo
            else -> membro.setor == filtro
        }

        correspondeBusca && correspondeFiltro
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = Screen.Members.route,
                onNavigate = onNavigateBottomBar
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "FluEJ",
                    fontWeight = FontWeight.Bold
                )

                Text("Gestão de membros")
            }

            if (viewModel.carregando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            BarraBusca(
                texto = busca,
                onTextoChange = { busca = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )

            FiltrosMembros(
                filtros = filtros,
                filtroSelecionado = filtro,
                onFiltroChange = { filtro = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 12.dp,
                    vertical = 4.dp
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(
                    items = membrosFiltrados,
                    key = { it.id }
                ) { membro ->
                    CardMembro(
                        membro = membro,
                        onClick = {
                            onMembroClick(membro)
                            if (viewModel.podeGerenciar) membroSelecionado = membro
                        }
                    )
                }

                if (membrosFiltrados.isEmpty() && !viewModel.carregando) {
                    item {
                        Text(
                            text = "Nenhum membro encontrado.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    membroSelecionado?.let { membro ->
        AlertDialog(
            onDismissRequest = { membroSelecionado = null },
            title = { Text(if (membro.ativo) "Inativar membro" else "Reativar membro") },
            text = {
                Text(
                    if (membro.ativo) {
                        "Deseja inativar ${membro.nome}? O histórico de pontos será mantido."
                    } else {
                        "Deseja reativar ${membro.nome}?"
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.alternarAtivo(membro)
                    membroSelecionado = null
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { membroSelecionado = null }) { Text("Cancelar") }
            }
        )
    }
}