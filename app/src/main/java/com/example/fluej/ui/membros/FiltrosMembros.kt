package com.example.fluej.ui.membros

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FiltrosMembros(
    filtros: List<String>,
    filtroSelecionado: String,
    onFiltroChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        filtros.forEach { filtro ->
            FilterChip(
                selected = filtroSelecionado == filtro,
                onClick = { onFiltroChange(filtro) },
                label = { Text(filtro) }
            )
        }
    }
}