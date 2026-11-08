package com.example.fluej.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.DesempenhoMensal
import com.example.fluej.data.formatarHoras
import com.example.fluej.data.sampleDataDesempenho
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.DashboardOrientadorViewModel
import java.text.NumberFormat
import java.util.Locale
import com.example.fluej.ui.components.SinoNotificacoes
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextDecoration
import com.example.fluej.ui.theme.GrayHeaderTable
import com.example.fluej.ui.viewmodels.RankingItem

@Composable
fun DashboardOrientadorScreen(
    onNavigateBottomBar: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardOrientadorViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.atualizar() }

    DashboardOrientadorConteudo(
        totalMembros = viewModel.totalMembros,
        totalProjetos = viewModel.totalProjetos,
        totalPontos = viewModel.totalPontos,
        totalHoras = viewModel.totalHoras,
        desempenho = viewModel.desempenho,
        ranking = viewModel.ranking,
        carregando = viewModel.carregando,
        onNavigateBottomBar = onNavigateBottomBar,
        onLogout = onLogout
    )
}
private const val RANKING_RESUMIDO = 2
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardOrientadorConteudo(
    totalMembros: Int,
    totalProjetos: Int,
    totalPontos: Int,
    totalHoras: Double,
    desempenho: List<DesempenhoMensal>,
    ranking: List<RankingItem>,
    carregando: Boolean,
    onNavigateBottomBar: (String) -> Unit,
    onLogout: () -> Unit
) {
    val formatoInteiro = NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR"))
    var desempenhoExpandido by remember { mutableStateOf(false) }
    var rankingExpandido by remember { mutableStateOf(false) }
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
                    text = "Olá, Orientador!",
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
                text = "Ranking de pontuações",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )
            val rankingVisivel = if (rankingExpandido) ranking else ranking.take(RANKING_RESUMIDO)
            val maxPontos = ranking.maxOfOrNull { it.pontos } ?: 0
            if (ranking.isEmpty()) {
                Text(
                    text = "Nenhum membro cadastrado ainda.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            } else {
                rankingVisivel.forEach { item ->
                    LinhaRanking(nome = item.nome, pontos = item.pontos, maximo = maxPontos)
                }
                if (ranking.size > RANKING_RESUMIDO) {
                    Text(
                        text = if (rankingExpandido) "Ver menos" else "Ver mais",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { rankingExpandido = !rankingExpandido }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Composable
fun CartaoMetrica(valor: String, rotulo: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, GrayBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(GrayBorder, RoundedCornerShape(6.dp))
            )
            Column {
                Text(valor, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(rotulo, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun GraficoBarras(valores: List<DesempenhoMensal>) {
    val maximo = (valores.maxOfOrNull { it.pontos } ?: 1).coerceAtLeast(1)
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            valores.forEach { registro ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(registro.pontos.toFloat() / maximo)
                            .width(20.dp)
                            .background(
                                color = TextSecondary,
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(PrimaryDark)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            valores.forEach { registro ->
                Text(
                    text = registro.mes,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
@Composable
fun LinhaRanking(nome: String, pontos: Int, maximo: Int) {
    val fracao = (pontos.toFloat() / maximo.coerceAtLeast(1)).coerceIn(0f, 1f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, GrayBorder)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fracao)
                    .fillMaxHeight()
                    .background(GrayBorder, RoundedCornerShape(50))
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(PrimaryDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = CardBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = nome,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(text = "$pontos pts", fontSize = 12.sp, color = TextPrimary)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun DashboardOrientadorPreview() {
    FluEJTheme {
        DashboardOrientadorConteudo(
            totalMembros = 28,
            totalProjetos = 5,
            totalPontos = 2450,
            totalHoras = 245.0,
            desempenho = sampleDataDesempenho,
            ranking = listOf(
                RankingItem("Ana Clara", 60),
                RankingItem("Bruno Lima", 55),
                RankingItem("Carla Souza", 40)
            ),
            carregando = false,
            onNavigateBottomBar = {},
            onLogout = {}
        )
    }
}