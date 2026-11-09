package com.example.fluej.ui.pontos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluej.data.pontos.PontosPassivosCargo
import com.example.fluej.data.pontos.PontosPassivosRepository
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import kotlinx.coroutines.launch

private data class CargoPontos(
    val id: Long,
    val cargo: String,
    val pontos: String
)

@Composable
fun TelaPontosScreen(
    onBack: () -> Unit = {},
    onNavigateBottomBar: (String) -> Unit = {}
) {
    val repository = remember { PontosPassivosRepository() }

    val linhas = remember {
        mutableStateListOf<CargoPontos>()
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    var carregando by remember {
        mutableStateOf(true)
    }

    var salvando by remember {
        mutableStateOf(false)
    }

    var creditando by remember {
        mutableStateOf(false)
    }

    // Carrega os cargos e suas pontuações do Supabase.
    LaunchedEffect(Unit) {
        try {
            val cargos = repository.buscarCargos()

            linhas.clear()
            linhas.addAll(
                cargos.map {
                    CargoPontos(
                        id = it.id,
                        cargo = it.cargo,
                        pontos = it.pontosSemanais.toString()
                    )
                }
            )
        } catch (e: Exception) {
            snackbarHostState.showSnackbar(
                "Erro ao carregar os cargos: ${e.message}"
                    .take(180)
            )
        } finally {
            carregando = false
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = Screen.Pontos.route,
                onNavigate = onNavigateBottomBar
            )
        },
        containerColor = Color(0xFFF1F1F1)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 14.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "FluEJ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Text(
                    text = "Gestão de membros",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 18.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "PONTOS PASSIVOS POR CARGO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (carregando) {
                    Text(
                        text = "Carregando cargos...",
                        color = Color.DarkGray
                    )
                } else {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFBDBDBD))
                            .border(
                                1.dp,
                                Color(0xFFAAAAAA)
                            )
                            .padding(
                                vertical = 8.dp,
                                horizontal = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cargo",
                            modifier = Modifier.weight(1f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Pontos\nsemanais",
                            modifier = Modifier.weight(0.65f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .border(
                                1.dp,
                                Color(0xFFAAAAAA)
                            ),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(
                            items = linhas,
                            key = { it.id }
                        ) { linha ->

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White)
                                    .border(
                                        width = 0.5.dp,
                                        color = Color(0xFFBBBBBB)
                                    )
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 3.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = linha.cargo,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 4.dp),
                                    fontSize = 10.sp,
                                    color = Color.DarkGray
                                )

                                OutlinedTextField(
                                    value = linha.pontos,
                                    onValueChange = { novoValor ->

                                        if (
                                            novoValor.all { it.isDigit() } &&
                                            novoValor.length <= 5
                                        ) {
                                            val indice =
                                                linhas.indexOfFirst {
                                                    it.id == linha.id
                                                }

                                            if (indice >= 0) {
                                                linhas[indice] =
                                                    linhas[indice].copy(
                                                        pontos = novoValor
                                                    )
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(0.65f),
                                    singleLine = true,
                                    enabled = !salvando && !creditando,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(
                                        textAlign = TextAlign.Center,
                                        fontSize = 11.sp
                                    ),
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val valoresValidos = linhas.all {
                                it.pontos.isNotBlank() &&
                                    it.pontos.toIntOrNull() != null
                            }

                            if (!valoresValidos) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Preencha todos os campos corretamente."
                                    )
                                }
                            } else {
                                scope.launch {
                                    salvando = true

                                    try {
                                        val cargos =
                                            linhas.map {
                                                PontosPassivosCargo(
                                                    id = it.id,
                                                    cargo = it.cargo,
                                                    pontosSemanais =
                                                        it.pontos.toInt()
                                                )
                                            }

                                        repository.salvarTodos(cargos)

                                        snackbarHostState.showSnackbar(
                                            "Pontuações salvas com sucesso!"
                                        )
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(
                                            "Erro ao salvar: ${e.message}"
                                                .take(180)
                                        )
                                    } finally {
                                        salvando = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(46.dp),
                        enabled = !salvando && !creditando,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF252525)
                        )
                    ) {
                        Text(
                            text = if (salvando) {
                                "Salvando..."
                            } else {
                                "Salvar"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                creditando = true

                                try {
                                    val total =
                                        repository.creditarPontosSemanais()

                                    snackbarHostState.showSnackbar(
                                        "$total lançamento(s) realizado(s) nesta semana."
                                    )
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar(
                                        "Erro ao creditar pontos: ${e.message}"
                                            .take(180)
                                    )
                                } finally {
                                    creditando = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(46.dp),
                        enabled = !salvando && !creditando,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF454545)
                        )
                    ) {
                        Text(
                            text = if (creditando) {
                                "Creditando..."
                            } else {
                                "Creditar pontos da semana"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}