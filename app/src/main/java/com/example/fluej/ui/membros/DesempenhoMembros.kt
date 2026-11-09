package com.example.fluej.ui.membros

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.model.DesempenhoMembro
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.viewmodels.DesempenhoViewModel

@Composable
fun TelaDesempenhoScreen(
    membroId: String,
    onBack: () -> Unit,
    onNavigateBottomBar: (String) -> Unit,
    viewModel: DesempenhoViewModel = viewModel()
) {
    var meses by remember { mutableIntStateOf(6) }

    LaunchedEffect(membroId, meses) {
        viewModel.carregar(membroId, meses)
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = Screen.Members.route,
                onNavigate = onNavigateBottomBar
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            TextButton(onClick = onBack) {
                Text("← Voltar para membros")
            }

            Text(
                text = "Desempenho individual",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(3, 6, 12, 24).forEach { periodo ->
                    FilterChip(
                        selected = meses == periodo,
                        onClick = { meses = periodo },
                        label = { Text("${periodo}m") }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (viewModel.carregando) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Carregando desempenho...")
            }

            viewModel.erro?.let { mensagem ->
                Text(
                    text = mensagem,
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = {
                        viewModel.carregar(membroId, meses)
                    }
                ) {
                    Text("Tentar novamente")
                }
            }

            viewModel.desempenho?.let { dados ->
                ConteudoDesempenho(dados)
            }
        }
    }
}

@Composable
private fun ConteudoDesempenho(
    dados: DesempenhoMembro
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = dados.membro.nome,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "${dados.membro.cargo} • ${dados.membro.setor}"
                    )

                    Text(
                        text = if (dados.membro.ativo) "Ativo" else "Inativo",
                        color = if (dados.membro.ativo)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IndicadorDesempenho(
                    titulo = "Total de pontos",
                    valor = dados.totalPontos.toString(),
                    modifier = Modifier.weight(1f)
                )

                IndicadorDesempenho(
                    titulo = "Horas acumuladas",
                    valor = String.format(
                        java.util.Locale("pt", "BR"),
                        "%.2f h",
                        dados.totalHoras
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "Histórico mensal",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (dados.pontosPorMes.isEmpty()) {
            item {
                Text("Nenhuma atividade encontrada no período.")
            }
        } else {
            val maiorPontuacao = dados.pontosPorMes
                .maxOf { it.pontos }
                .coerceAtLeast(1)

            items(dados.pontosPorMes) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = item.mes,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text("${item.pontos} pontos • ${item.horas} horas")

                        LinearProgressIndicator(
                            progress = {
                                item.pontos.toFloat() / maiorPontuacao
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Projetos vinculados",
                style = MaterialTheme.typography.titleMedium
            )
        }

        if (dados.projetos.isEmpty()) {
            item {
                Text("Nenhum projeto vinculado.")
            }
        } else {
            items(dados.projetos) { projeto ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = projeto.nome,
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text("${projeto.totalPontos} pontos")
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadorDesempenho(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}