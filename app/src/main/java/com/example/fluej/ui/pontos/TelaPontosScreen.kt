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
import androidx.compose.foundation.layout.weight
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private data class CargoPontos(
    val id: Int,
    val cargo: String,
    val pontos: String
)

@Composable
fun TelaPontosScreen(
    onNavigate: (String) -> Unit = {}
) {
    val linhas = remember {
        mutableStateListOf(
            CargoPontos(1, "Presidente", "20"),
            CargoPontos(2, "Gestor de RH", "15"),
            CargoPontos(3, "Membro de RH", "10"),
            CargoPontos(4, "Gestor de Projetos", "15"),
            CargoPontos(5, "Membro de Projetos", "5"),
            CargoPontos(6, "Gestor de Projetos", "15"),
            CargoPontos(7, "Membro de Projetos", "5"),
            CargoPontos(8, "Gestor de Marketing", "5")
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            BarraInferiorPontos(onNavigate = onNavigate)
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
                    .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    .padding(horizontal = 16.dp, vertical = 18.dp),
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

                // Cabeçalho da tabela
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFBDBDBD))
                        .border(1.dp, Color(0xFFAAAAAA))
                        .padding(vertical = 8.dp, horizontal = 10.dp),
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
                        .border(1.dp, Color(0xFFAAAAAA)),
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
                                    if (novoValor.all { it.isDigit() } &&
                                        novoValor.length <= 5
                                    ) {
                                        val indice = linhas.indexOfFirst {
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

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val valoresValidos = linhas.all {
                            it.pontos.isNotBlank()
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (valoresValidos) {
                                    "Valores preparados! A persistência será integrada ao banco de dados."
                                } else {
                                    "Preencha todos os campos de pontos."
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF252525)
                    )
                ) {
                    Text(
                        text = "Salvar",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun BarraInferiorPontos(
    onNavigate: (String) -> Unit
) {
    val itens = listOf(
        "Início" to "⌂",
        "Membros" to "♧",
        "Pontos" to "☆",
        "Relatórios" to "▥"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(
                width = 0.5.dp,
                color = Color.LightGray
            )
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        itens.forEach { (nome, simbolo) ->
            val selecionado = nome == "Pontos"

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = simbolo,
                    fontSize = 22.sp,
                    color = if (selecionado) {
                        Color.Black
                    } else {
                        Color.Gray
                    }
                )

                Text(
                    text = nome,
                    fontSize = 10.sp,
                    fontWeight = if (selecionado) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    color = if (selecionado) {
                        Color.Black
                    } else {
                        Color.DarkGray
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}