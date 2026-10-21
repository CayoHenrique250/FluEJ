package com.example.fluej.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluej.data.Options
import com.example.fluej.data.Periodo
import com.example.fluej.data.ReportDates
import com.example.fluej.data.XlsxWriter
import com.example.fluej.data.formatarHoras
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.ReportsViewModel
import com.example.fluej.ui.viewmodels.activityReportsViewModel
import java.text.NumberFormat
import java.util.Locale

private const val TODOS_CARGOS = "Todos os cargos"
private const val TODOS_MEMBROS = "Todos os membros"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateBottomBar: (String) -> Unit,
    onGenerateReport: () -> Unit,
    viewModel: ReportsViewModel = activityReportsViewModel()
) {
    val context = LocalContext.current
    var mostrarCalendario by remember { mutableStateOf(false) }
    val relatorio = viewModel.relatorio
    val temDados = relatorio != null && relatorio.linhas.isNotEmpty()

    LaunchedEffect(Unit) { viewModel.atualizar() }

    LaunchedEffect(viewModel.mensagem) {
        viewModel.mensagem?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.limparMensagem()
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument(XlsxWriter.MIME)
    ) { uri -> if (uri != null) viewModel.exportar(uri) }

    val membrosFiltrados = viewModel.membrosFiltrados
    val formatoInteiro = NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR"))

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
                selectedTab = Screen.Reports.route,
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

            Text("Período", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Periodo.values().forEach { period ->
                    val isSelected = viewModel.periodo == period
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryDark else UnselectedChipBg)
                            .border(1.dp, if (isSelected) PrimaryDark else GrayBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.selecionarPeriodo(period) }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = period.rotulo,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = "${ReportDates.br(viewModel.inicio)} - ${ReportDates.br(viewModel.fim)}",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { mostrarCalendario = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Calendário",
                                tint = TextSecondary
                            )
                        }
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
                        .clickable { mostrarCalendario = true }
                )
            }

            Text("Filtros", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)

            DropdownSelectField(
                label = "Cargo",
                selectedValue = viewModel.cargo ?: TODOS_CARGOS,
                options = listOf(TODOS_CARGOS) + Options.cargos,
                placeholder = TODOS_CARGOS,
                onOptionSelected = { escolha ->
                    viewModel.selecionarCargo(if (escolha == TODOS_CARGOS) null else escolha)
                }
            )

            DropdownSelectField(
                label = "Membro",
                selectedValue = membrosFiltrados.firstOrNull { it.id == viewModel.membroId }?.nome
                    ?: TODOS_MEMBROS,
                options = listOf(TODOS_MEMBROS) + membrosFiltrados.map { it.nome },
                placeholder = TODOS_MEMBROS,
                onOptionSelected = { escolha ->
                    val membro = membrosFiltrados.firstOrNull { it.nome == escolha }
                    viewModel.selecionarMembro(if (escolha == TODOS_MEMBROS) null else membro?.id)
                }
            )

            if (relatorio != null && relatorio.linhas.isEmpty()) {
                Text(
                    "Nenhum membro encontrado para esses filtros.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Button(
                onClick = onGenerateReport,
                enabled = temDados,
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
                ResumoCard(
                    valor = formatoInteiro.format(relatorio?.totalPontos ?: 0),
                    rotulo = "Total de pontos",
                    modifier = Modifier.weight(1f)
                )
                ResumoCard(
                    valor = formatarHoras(relatorio?.totalHoras ?: 0.0),
                    rotulo = "Total de horas",
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = { exportLauncher.launch(viewModel.nomeArquivo()) },
                enabled = temDados,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
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

    if (mostrarCalendario) {
        DateRangeDialog(
            inicio = viewModel.inicio,
            fim = viewModel.fim,
            onConfirm = { novoInicio, novoFim ->
                viewModel.definirIntervalo(novoInicio, novoFim)
                mostrarCalendario = false
            },
            onDismiss = { mostrarCalendario = false }
        )
    }
}

@Composable
private fun ResumoCard(valor: String, rotulo: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
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
                Text(valor, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(rotulo, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateRangeDialog(
    inicio: String,
    fim: String,
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDateRangePickerState(
        initialSelectedStartDateMillis = ReportDates.millisUtcDeIso(inicio),
        initialSelectedEndDateMillis = ReportDates.millisUtcDeIso(fim)
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = state.selectedStartDateMillis != null,
                onClick = {
                    val start = state.selectedStartDateMillis
                    val end = state.selectedEndDateMillis ?: start
                    if (start != null && end != null) {
                        onConfirm(ReportDates.isoDeMillisUtc(start), ReportDates.isoDeMillisUtc(end))
                    }
                }
            ) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    ) {
        DateRangePicker(state = state, modifier = Modifier.height(500.dp))
    }
}