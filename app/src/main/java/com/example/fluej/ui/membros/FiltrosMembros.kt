package com.example.fluej.ui.membros

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FiltrosMembros(
    filtroSelecionado: String,
    onFiltroChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf("Todos", "Ativos", "Inativos").forEach { filtro ->
            FilterChip(
                selected = filtroSelecionado == filtro,
                onClick = { onFiltroChange(filtro) },
                label = { Text(filtro) }
            )
        }
    }
}