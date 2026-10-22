package com.example.fluej.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.formatarHoras
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

private const val RANKING_RESUMIDO = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToRegisterProject: () -> Unit,
    onNavigateBottomBar: (String) -> Unit,
    onNavigateToMembers: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val context = LocalContext.current
    var rankingExpandido by remember { mutableStateOf(false) }
    val emBreve = { Toast.makeText(context, "Em breve", Toast.LENGTH_SHORT).show() }

    LaunchedEffect(Unit) { viewModel.atualizar() }

    val formatoInteiro = NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR"))
    val ranking = viewModel.ranking
    val visiveis = if (rankingExpandido) ranking else ranking.take(RANKING_RESUMIDO)
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
                    IconButton(onClick = emBreve) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = TextPrimary)
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
            if (viewModel.carregando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Column {
                Text(
                    text = "Olá, Gestor de ${viewModel.setor}!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Aqui está um resumo do seu setor.",
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    valor = formatoInteiro.format(viewModel.totalMembros),
                    rotulo = "Membros do setor",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    valor = viewModel.totalProjetos.toString(),
                    rotulo = "Projetos ativos",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    valor = formatoInteiro.format(viewModel.totalPontos),
                    rotulo = "Total de pontos",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    valor = formatarHoras(viewModel.totalHoras),
                    rotulo = "Total de horas",
                    modifier = Modifier.weight(1f)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ranking de pontuação", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Text("Pontos acumulados no mês atual", fontSize = 11.sp, color = TextSecondary)

                if (ranking.isEmpty()) {
                    Text(
                        "Nenhum membro cadastrado neste setor ainda.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                } else {
                    visiveis.forEach { item ->
                        val fracao = if (maxPontos > 0) {
                            (item.pontos.toFloat() / maxPontos).coerceIn(0.12f, 1f)
                        } else 0.12f
                        RankingRow(
                            nome = item.nome,
                            pontosTexto = "${formatoInteiro.format(item.pontos)} pts",
                            fracao = fracao
                        )
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
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Documentos pendentes", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                    Text(
                        "Ver todos >",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.clickable { emBreve() }
                    )
                }
                DocumentoPendenteRow("Termos de voluntariado", 2)
                DocumentoPendenteRow("Termos de encerramento", 5)
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ações rápidas", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AcaoRapida("Registrar projeto", Icons.Default.Folder, Modifier.weight(1f), onNavigateToRegisterProject)
                    AcaoRapida("Registrar pontos", Icons.Default.Star, Modifier.weight(1f), emBreve)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AcaoRapida("Cadastrar membro", Icons.Default.PersonAdd, Modifier.weight(1f), onNavigateToRegister)
                    AcaoRapida("Inativar membro", Icons.Default.Block, Modifier.weight(1f), emBreve)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(valor: String, rotulo: String, modifier: Modifier = Modifier) {
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
private fun RankingRow(nome: String, pontosTexto: String, fracao: Float) {
    val forma = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(forma)
            .background(CardBackground)
            .border(1.dp, GrayBorder, forma)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(fracao)
                .fillMaxHeight()
                .background(GrayHeaderTable)
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = nome,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(pontosTexto, fontSize = 13.sp, color = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun DocumentoPendenteRow(titulo: String, quantidade: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, GrayBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(titulo, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
            Text(quantidade.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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