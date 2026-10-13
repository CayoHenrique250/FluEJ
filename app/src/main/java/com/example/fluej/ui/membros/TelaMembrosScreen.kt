package com.example.fluej.ui.membros

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.navigation.Screen

@Composable
fun TelaMembrosScreen(
    onMembroClick: (Membro) -> Unit = {},
    onNavigateBottomBar: (String) -> Unit
) {
    var busca by remember { mutableStateOf("") }
    var filtro by remember { mutableStateOf("Todos") }

    val membros = remember {
        listOf(
            Membro("Anninha Clarinha", "Gestor(a) RH", 120, true),
            Membro("Barbatana Lady", "Gestor(a) Marketing", 10, true),
            Membro("Creuza de Jesus", "Membro de RH", 220, true),
            Membro("Julinho Cavalieri", "Presidente", 520, true),
            Membro("Luanzinho do Grau", "Membro de projetos", 1560, false)
        )
    }

    fun obterSetor(cargo: String): String {
        return when {
            cargo.contains("marketing", ignoreCase = true) ->
                "Marketing"

            cargo.contains("projetos", ignoreCase = true) ->
                "Projetos"

            cargo.contains("rh", ignoreCase = true) ->
                "RH"

            cargo.contains("vendas", ignoreCase = true) ->
                "Vendas"

            else -> ""
        }
    }

    val membrosFiltrados = membros.filter { membro ->
        val correspondeBusca =
            membro.nome.contains(busca, ignoreCase = true) ||
                    membro.cargo.contains(busca, ignoreCase = true)

        val setor = obterSetor(membro.cargo)

        val correspondeFiltro = when (filtro) {
            "Ativos" -> membro.ativo
            "Inativos" -> !membro.ativo
            "Marketing", "Projetos", "RH", "Vendas" ->
                setor == filtro

            else -> true
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

            BarraBusca(
                texto = busca,
                onTextoChange = { busca = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )

            FiltrosMembros(
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
                    key = { it.nome }
                ) { membro ->
                    CardMembro(
                        membro = membro,
                        onClick = { onMembroClick(membro) }
                    )
                }

                if (membrosFiltrados.isEmpty()) {
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
}