package com.example.fluej.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.api.NotificacaoDto
import com.example.fluej.ui.theme.*
import com.example.fluej.ui.viewmodels.NotificacoesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinoNotificacoes(viewModel: NotificacoesViewModel = viewModel()) {
    var aberto by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.atualizar() }

    IconButton(onClick = { aberto = true }) {
        BadgedBox(
            badge = {
                if (viewModel.itens.isNotEmpty()) {
                    Badge { Text(viewModel.itens.size.toString()) }
                }
            }
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = TextPrimary)
        }
    }

    if (aberto) {
        ModalBottomSheet(
            onDismissRequest = { aberto = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = SurfaceLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text("Notificações", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                if (viewModel.offline) {
                    Text("Mostrando dados salvos no aparelho.", fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(12.dp))

                when {
                    viewModel.carregando && viewModel.itens.isEmpty() ->
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

                    viewModel.erro != null && viewModel.itens.isEmpty() -> {
                        Text(viewModel.erro.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        TextButton(onClick = { viewModel.atualizar() }) { Text("Tentar novamente") }
                    }

                    viewModel.itens.isEmpty() ->
                        Text("Nenhuma notificação por enquanto.", fontSize = 13.sp, color = TextSecondary)

                    else -> LazyColumn(
                        modifier = Modifier.heightIn(max = 420.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.itens, key = { it.id }) { NotificacaoItem(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificacaoItem(item: NotificacaoDto) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, GrayBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.tipo == "feriado") Icons.Default.DateRange else Icons.Default.Info,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                if (item.mensagem.isNotBlank()) {
                    Text(item.mensagem, fontSize = 11.sp, color = TextSecondary)
                }
            }
            Text(dataCurta(item.data), fontSize = 11.sp, color = TextSecondary)
        }
    }
}

private fun dataCurta(iso: String): String {
    val p = iso.split("-")
    return if (p.size == 3) "${p[2]}/${p[1]}" else iso
}