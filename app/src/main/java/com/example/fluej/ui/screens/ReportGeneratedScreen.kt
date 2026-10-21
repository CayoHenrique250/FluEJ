package com.example.fluej.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluej.data.ReportDates
import com.example.fluej.data.XlsxWriter
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.ReportsViewModel
import com.example.fluej.ui.viewmodels.activityReportsViewModel

private fun larguraColuna(indice: Int): Dp = if (indice == 0) 150.dp else 110.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportGeneratedScreen(
    onNavigateBottomBar: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ReportsViewModel = activityReportsViewModel()
) {
    val context = LocalContext.current
    val relatorio = viewModel.relatorio

    LaunchedEffect(viewModel.mensagem) {
        viewModel.mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limparMensagem()
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(XlsxWriter.MIME)
    ) { uri -> if (uri != null) viewModel.exportar(uri) }

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
                selectedTab = Screen.ReportGenerated.route,
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

            if (relatorio == null) {
                Text("Nenhum relatório disponível. Volte e gere novamente.", color = TextSecondary)
            } else {
                Text(
                    text = "Período: ${ReportDates.br(relatorio.inicio)} a ${ReportDates.br(relatorio.fim)}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Cargo: ${viewModel.cargo ?: "Todos"}  •  Membro: " +
                            (viewModel.todosMembros.firstOrNull { it.id == viewModel.membroId }?.nome ?: "Todos"),
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                if (relatorio.totalPontos == 0 && relatorio.totalHoras == 0.0) {
                    Text(
                        "Não há pontos ou horas lançados neste período.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

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
                            relatorio.cabecalho.forEachIndexed { i, header ->
                                Text(
                                    text = header,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(larguraColuna(i)),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    color = TextPrimary
                                )
                            }
                        }

                        HorizontalDivider(color = GrayBorder)

                        relatorio.linhasTexto().forEach { row ->
                            Row(modifier = Modifier.padding(vertical = 12.dp)) {
                                row.forEachIndexed { i, cell ->
                                    Text(
                                        text = cell,
                                        modifier = Modifier.width(larguraColuna(i)),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        color = TextPrimary
                                    )
                                }
                            }
                            HorizontalDivider(color = GrayBorder)
                        }

                        Row(
                            modifier = Modifier
                                .background(GrayHeaderTable)
                                .padding(vertical = 12.dp)
                        ) {
                            relatorio.totalTexto().forEachIndexed { i, cell ->
                                Text(
                                    text = cell,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(larguraColuna(i)),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { exportLauncher.launch(viewModel.nomeArquivo()) },
                enabled = relatorio != null && relatorio.linhas.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
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