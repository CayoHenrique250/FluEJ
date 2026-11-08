package com.example.fluej.ui.pontos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.navigation.Screen
import androidx.compose.ui.text.input.KeyboardType

private val FundoTela = Color(0xFFF2F2F2)
private val PretoFluEJ = Color(0xFF252525)

@Composable
fun TelaRegistrarPontosScreen(
    onBack: () -> Unit,
    
    membros: List<String> = listOf(
        "Anna Clara",
        "Cayo Henrique",
        "Emilly Vitória",
        "Jousiclecia",
        "Sara Naillê"
    ),

    projetos: List<String> = listOf(
        "Projeto FluEJ",
        "Projeto SOSHealth",
        "Projeto Espaços Maker"
    ),

    onSalvar: (String, String, String, Int) -> Unit = { _, _, _, _ -> },

    onNavigateBottomBar: (String) -> Unit = {}
) {

    var membroSelecionado by remember { mutableStateOf("") }
    var projetoSelecionado by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var pontos by remember { mutableStateOf("0") }

    var erro by remember { mutableStateOf("") }

    Scaffold(
        containerColor = FundoTela,


        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White)
                    .padding(horizontal = 16.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "FluEJ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PretoFluEJ
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Gestão de membros",
                    fontSize = 12.sp,
                    color = PretoFluEJ
                )
            }
        },

        bottomBar = {
            BottomNavBar(
                selectedTab = Screen.Pontos.route,
                onNavigate = onNavigateBottomBar
            )
        }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ADICIONAR PONTOS",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PretoFluEJ,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Membro",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PretoFluEJ
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoSelecaoPontos(
                valor = membroSelecionado,
                placeholder = "Selecione o membro",
                opcoes = membros,
                onSelecionar = {
                    membroSelecionado = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Projeto",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PretoFluEJ
            )

            Spacer(modifier = Modifier.height(8.dp))

            CampoSelecaoPontos(
                valor = projetoSelecionado,
                placeholder = "Selecione o projeto",
                opcoes = projetos,
                onSelecionar = {
                    projetoSelecionado = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Descrição",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PretoFluEJ
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = {
                    descricao = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),

                placeholder = {
                    Text(
                        text = "Ex: Projeto xxx",
                        fontSize = 11.sp
                    )
                },

                shape = RoundedCornerShape(4.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),

                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Pontos",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PretoFluEJ
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pontos,
                onValueChange = { novoValor ->
                    if (novoValor.all { it.isDigit() }) {
                        pontos = novoValor
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                singleLine = true,

                shape = RoundedCornerShape(4.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),

                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp
                )
            )

            if (erro.isNotEmpty()) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = erro,
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {

                    if (
                        membroSelecionado.isBlank() ||
                        projetoSelecionado.isBlank() ||
                        descricao.isBlank() ||
                        pontos.isBlank()
                    ) {

                        erro = "Preencha todos os campos."

                    } else {

                        erro = ""

                        onSalvar(
                            membroSelecionado,
                            projetoSelecionado,
                            descricao,
                            pontos.toInt()
                        )
                    }
                },

                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(164.dp)
                    .height(42.dp),

                shape = RoundedCornerShape(12.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = PretoFluEJ,
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Salvar",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}


@Composable
fun CampoSelecaoPontos(
    valor: String,
    placeholder: String,
    opcoes: List<String>,
    onSelecionar: (String) -> Unit
) {

    var expandido by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(
                    Color.White,
                    RoundedCornerShape(4.dp)
                )
                .clickable {
                    expandido = true
                }
                .padding(horizontal = 10.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = if (valor.isEmpty()) placeholder else valor,
                modifier = Modifier.weight(1f),
                fontSize = 11.sp,
                color = if (valor.isEmpty())
                    Color.DarkGray else PretoFluEJ
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Selecionar",
                tint = Color.Gray
            )
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                expandido = false
            },

            modifier = Modifier
                .background(Color.White)
        ) {

            opcoes.forEach { opcao ->

                DropdownMenuItem(
                    text = {
                        Text(
                            text = opcao,
                            fontSize = 12.sp
                        )
                    },

                    onClick = {
                        onSelecionar(opcao)
                        expandido = false
                    }
                )
            }
        }
    }
}