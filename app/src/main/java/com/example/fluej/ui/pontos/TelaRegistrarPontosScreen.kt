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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.Member
import com.example.fluej.data.Project
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import com.example.fluej.ui.viewmodels.PontosViewModel

private val FundoTela = Color(0xFFF2F2F2)
private val PretoFluEJ = Color(0xFF252525)

@Composable
fun TelaRegistrarPontosScreen(
    onBack: () -> Unit,
    onNavigateBottomBar: (String) -> Unit = {},
    pontosViewModel: PontosViewModel = viewModel()
) {
    val membros = pontosViewModel.membros
    val projetos = pontosViewModel.projetos
    val membroSelecionado = pontosViewModel.membroSelecionado
    val projetoSelecionado = pontosViewModel.projetoSelecionado
    val carregando = pontosViewModel.carregando
    val salvando = pontosViewModel.salvando
    val mensagem = pontosViewModel.mensagem.orEmpty()
    val erroViewModel = pontosViewModel.erro.orEmpty()

    var descricao by remember { mutableStateOf("") }
    var pontos by remember { mutableStateOf("") }
    var erroLocal by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        pontosViewModel.carregarDados()
    }

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
                valor = membroSelecionado?.nome.orEmpty(),
                placeholder = "Selecione o membro",
                opcoes = membros,
                textoOpcao = { it.nome },
                habilitado = !carregando && !salvando,
                onSelecionar = {
                    erroLocal = ""
                    pontosViewModel.selecionarMembro(it)
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
                valor = projetoSelecionado?.nome.orEmpty(),
                placeholder = if (membroSelecionado == null) {
                    "Selecione primeiro um membro"
                } else {
                    "Selecione o projeto"
                },
                opcoes = projetos,
                textoOpcao = { it.nome },
                habilitado = membroSelecionado != null &&
                    !carregando && !salvando,
                onSelecionar = {
                    erroLocal = ""
                    pontosViewModel.selecionarProjeto(it)
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
                    erroLocal = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                enabled = !salvando,
                placeholder = {
                    Text(
                        text = "Ex: Desenvolvimento de funcionalidade",
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

            Spacer(modifier = Modifier.height(16.dp))

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
                        erroLocal = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !salvando,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                placeholder = {
                    Text("Ex: 100", fontSize = 11.sp)
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

            if (carregando) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Carregando dados...",
                        fontSize = 11.sp,
                        color = Color.DarkGray
                    )
                }
            }

            val mensagemErro = erroLocal.ifBlank { erroViewModel }

            if (mensagemErro.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = mensagemErro,
                    color = Color.Red,
                    fontSize = 11.sp
                )
            }

            if (mensagem.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = mensagem,
                    color = Color(0xFF267A39),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    erroLocal = ""

                    val quantidade = pontos.toIntOrNull()

                    when {
                        membroSelecionado == null ->
                            erroLocal = "Selecione um membro."

                        projetoSelecionado == null ->
                            erroLocal = "Selecione um projeto."

                        descricao.isBlank() ->
                            erroLocal = "Informe a descrição."

                        quantidade == null || quantidade <= 0 ->
                            erroLocal = "Informe uma quantidade de pontos válida."

                        else -> {
                            pontosViewModel.salvarPontos(
                                descricao = descricao,
                                quantidade = quantidade
                            ) {
                                descricao = ""
                                pontos = ""
                            }
                        }
                    }
                },
                enabled = !carregando && !salvando,
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
                if (salvando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Salvar",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun <T> CampoSelecaoPontos(
    valor: String,
    placeholder: String,
    opcoes: List<T>,
    textoOpcao: (T) -> String,
    habilitado: Boolean = true,
    onSelecionar: (T) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .background(
                    if (habilitado) Color.White else Color(0xFFEAEAEA),
                    RoundedCornerShape(4.dp)
                )
                .clickable(enabled = habilitado) {
                    expandido = true
                }
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (valor.isEmpty()) placeholder else valor,
                modifier = Modifier.weight(1f),
                fontSize = 11.sp,
                color = if (valor.isEmpty()) {
                    Color.DarkGray
                } else {
                    PretoFluEJ
                }
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
            modifier = Modifier.background(Color.White)
        ) {
            if (opcoes.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Nenhuma opção disponível",
                            fontSize = 12.sp
                        )
                    },
                    onClick = {
                        expandido = false
                    },
                    enabled = false
                )
            }

            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = textoOpcao(opcao),
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