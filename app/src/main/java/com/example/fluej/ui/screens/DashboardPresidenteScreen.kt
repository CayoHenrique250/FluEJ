package com.example.fluej.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.DesempenhoMensal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.fluej.data.sampleDataDesempenho
import com.example.fluej.data.api.RankingItemDto
import com.example.fluej.data.formatarHoras
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.components.SinoNotificacoes
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.DashboardPresidenteViewModel
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.Alignment

@Composable
fun DashboardPresidenteScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateBottomBar: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardPresidenteViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.atualizar() }

    DashboardPresidenteConteudo(
        totalMembros = viewModel.totalMembros,
        totalProjetos = viewModel.totalProjetos,
        totalPontos = viewModel.totalPontos,
        totalHoras = viewModel.totalHoras,
        desempenho = viewModel.desempenho,
        ranking = viewModel.ranking,
        carregando = viewModel.carregando,
        usandoCache = viewModel.usandoCache,
        erro = viewModel.erro,
        onNavigateToRegister = onNavigateToRegister,
        onNavigateBottomBar = onNavigateBottomBar,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardPresidenteConteudo(
    totalMembros: Int,
    totalProjetos: Int,
    totalPontos: Int,
    totalHoras: Double,
    desempenho: List<DesempenhoMensal>,
    ranking: List<RankingItemDto>,
    carregando: Boolean,
    usandoCache: Boolean,
    erro: String?,
    onNavigateToRegister: () -> Unit,
    onNavigateBottomBar: (String) -> Unit,
    onLogout: () -> Unit
) {
    val formatoInteiro = NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR"))
    var desempenhoExpandido by remember { mutableStateOf(false) }
    val maxPontos = ranking.maxOfOrNull { it.pontos } ?: 0

    Scaffold(
        containerColor = SurfaceLight,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight),
                title = {
                    Text("FluEJ", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = TextPrimary)
                },
                actions = {
                    SinoNotificacoes()
                    TextButton(onClick = onLogout) {
                        Text("Sair", color = TextPrimary)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = Screen.Home.route,
                onNavigate = onNavigateBottomBar
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (carregando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Column {
                Text(
                    text = "Olá, Presidente!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Aqui está um resumo da sua EJ.",
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            if (usandoCache) {
                Text(
                    text = "Mostrando dados salvos no aparelho.",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            if (erro != null && ranking.isEmpty()) {
                Text(
                    text = erro,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CartaoMetrica(
                    valor = formatoInteiro.format(totalMembros),
                    rotulo = "Membros ativos",
                    modifier = Modifier.weight(1f)
                )
                CartaoMetrica(
                    valor = totalProjetos.toString(),
                    rotulo = "Projetos ativos",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CartaoMetrica(
                    valor = formatoInteiro.format(totalPontos),
                    rotulo = "Total de pontos",
                    modifier = Modifier.weight(1f)
                )
                CartaoMetrica(
                    valor = formatarHoras(totalHoras),
                    rotulo = "Total de horas",
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = BorderStroke(1.dp, GrayBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Desempenho geral", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                        Text(
                            text = if (desempenhoExpandido) "Ver menos" else "Ver mais",
                            fontSize = 12.sp,
                            color = TextPrimary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { desempenhoExpandido = !desempenhoExpandido }
                        )
                    }
                    Text("Pontos por mês", fontSize = 11.sp, color = TextSecondary)
                    GraficoBarras(valores = if (desempenhoExpandido) desempenho else desempenho.take(6))
                }
            }

            Text(
                text = "Membros com baixa pontuação",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            if (ranking.isEmpty()) {
                if (!carregando && erro == null) {
                    Text(
                        text = "Nenhum membro no ranking ainda.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            } else {
                ranking.takeLast(3).reversed().forEach { item ->
                    LinhaRanking(nome = item.nome, pontos = item.pontos, maximo = maxPontos)
                }
            }

            Text(
                text = "Ações rápidas",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AcaoRapida(
                    texto = "Cadastrar membros",
                    icone = Icons.Default.PersonAdd,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToRegister
                )
                AcaoRapida(
                    texto = "Registrar pontos",
                    icone = Icons.Default.Star,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateBottomBar(Screen.Pontos.route) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AcaoRapida(
    texto: String,
    icone: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GrayBorder),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(icone, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(texto, fontSize = 12.sp, color = TextPrimary, maxLines = 1)
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardPresidentePreview() {
    FluEJTheme {
        DashboardPresidenteConteudo(
            totalMembros = 28,
            totalProjetos = 5,
            totalPontos = 2450,
            totalHoras = 245.0,
            desempenho = sampleDataDesempenho,
            ranking = listOf(
                RankingItemDto("1", "Ana Clara", "Diretora", "Projetos", 90, 9.0),
                RankingItemDto("2", "Bruno Lima", "Analista", "Comercial", 75, 7.5),
                RankingItemDto("3", "Carla Souza", "Analista", "Financeiro", 60, 6.0),
                RankingItemDto("4", "Diego Alves", "Trainee", "Projetos", 20, 2.0),
                RankingItemDto("5", "Eduarda Reis", "Trainee", "Comercial", 10, 1.0)
            ),
            carregando = false,
            usandoCache = false,
            erro = null,
            onNavigateToRegister = {},
            onNavigateBottomBar = {},
            onLogout = {}
        )
    }
}