package com.example.fluej.ui.membros

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.Member
import com.example.fluej.data.MemberRepository
import com.example.fluej.navigation.Screen
import com.example.fluej.ui.components.BottomNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaInativarMembroScreen(
    onNavigateBottomBar: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val repository = remember {
        MemberRepository(
            AppDatabase.getDatabase(context).memberDao()
        )
    }

    var membros by remember { mutableStateOf<List<Member>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var salvando by remember { mutableStateOf(false) }
    var buscaAtivos by remember { mutableStateOf("") }
    var buscaInativos by remember { mutableStateOf("") }
    var filtroAtivos by remember { mutableStateOf("Todos") }
    var filtroInativos by remember { mutableStateOf("Todos") }
    var selecionado by remember { mutableStateOf<Member?>(null) }
    var erro by remember { mutableStateOf<String?>(null) }

    fun carregarMembros() {
        scope.launch {
            carregando = true
            erro = null

            try {
                repository.sincronizarPendentes()
                repository.baixarDoSupabase()
                membros = repository.getMembrosLocais()
            } catch (e: Exception) {
                erro = e.message ?: "Erro ao carregar membros."
            } finally {
                carregando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        carregarMembros()
    }

    fun filtrar(
        lista: List<Member>,
        busca: String,
        setor: String
    ): List<Member> {
        return lista.filter { membro ->
            membro.nome.contains(busca.trim(), ignoreCase = true) &&
                    (setor == "Todos" ||
                            membro.setor.equals(setor, ignoreCase = true))
        }
    }

    val membrosAtivos = filtrar(
        membros.filter { it.ativo },
        buscaAtivos,
        filtroAtivos
    )

    val membrosInativos = filtrar(
        membros.filter { !it.ativo },
        buscaInativos,
        filtroInativos
    )

    Scaffold(
        containerColor = Color(0xFFF1F1F1),
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("FluEJ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                Text("Gestão de membros", fontSize = 12.sp)
            }
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = Screen.InativarMembro.route,
                onNavigate = onNavigateBottomBar
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "INATIVAR MEMBRO",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                CampoBuscaMembro(buscaAtivos) {
                    buscaAtivos = it
                }
            }

            item {
                FiltrosSetorMembro(filtroAtivos) {
                    filtroAtivos = it
                }
            }

            if (carregando) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            erro?.let { mensagem ->
                item {
                    Text(
                        mensagem,
                        color = MaterialTheme.colorScheme.error
                    )
                    TextButton(onClick = { carregarMembros() }) {
                        Text("Tentar novamente")
                    }
                }
            }

            if (!carregando) {
                if (membrosAtivos.isEmpty()) {
                    item { Text("Nenhum membro ativo encontrado.") }
                }

                items(membrosAtivos, key = { it.id }) { membro ->
                    CartaoMembroSituacao(
                        membro = membro,
                        textoAcao = "Inativar",
                        onClick = { selecionado = membro }
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "ATIVAR MEMBRO",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                CampoBuscaMembro(buscaInativos) {
                    buscaInativos = it
                }
            }

            item {
                FiltrosSetorMembro(filtroInativos) {
                    filtroInativos = it
                }
            }

            if (!carregando) {
                if (membrosInativos.isEmpty()) {
                    item { Text("Nenhum membro inativo encontrado.") }
                }

                items(membrosInativos, key = { it.id }) { membro ->
                    CartaoMembroSituacao(
                        membro = membro,
                        textoAcao = "Ativar",
                        onClick = { selecionado = membro }
                    )
                }
            }
        }
    }

    selecionado?.let { membro ->
        val novoStatus = !membro.ativo

        AlertDialog(
            onDismissRequest = {
                if (!salvando) selecionado = null
            },
            title = {
                Text(if (novoStatus) "Ativar membro?" else "Inativar membro?")
            },
            text = {
                Text("Deseja alterar a situação de ${membro.nome}?")
            },
            confirmButton = {
                TextButton(
                    enabled = !salvando,
                    onClick = {
                        scope.launch {
                            salvando = true

                            try {
                                val sincronizado = repository.atualizarSituacao(
                                    membro = membro,
                                    ativo = novoStatus
                                )

                                membros = repository.getMembrosLocais()
                                selecionado = null

                                snackbar.showSnackbar(
                                    if (sincronizado) {
                                        "Situação atualizada com sucesso."
                                    } else {
                                        "Salvo no aparelho. Será enviado ao Supabase quando houver conexão."
                                    }
                                )
                            } catch (e: Exception) {
                                snackbar.showSnackbar(e.message ?: "Erro ao atualizar membro.")
                            } finally {
                                salvando = false
                            }
                        }
                    }
                ) {
                    Text(if (salvando) "Salvando..." else "Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !salvando,
                    onClick = { selecionado = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CampoBuscaMembro(
    texto: String,
    onTextoChange: (String) -> Unit
) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar membro...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun FiltrosSetorMembro(
    selecionado: String,
    onSelecionar: (String) -> Unit
) {
    val setores = listOf(
        "Todos", "Projetos", "Marketing",
        "RH", "Vendas", "Comercial", "Financeiro"
    )

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        setores.forEach { setor ->
            FilterChip(
                selected = selecionado == setor,
                onClick = { onSelecionar(setor) },
                label = { Text(setor, fontSize = 11.sp) }
            )
        }
    }
}

@Composable
private fun CartaoMembroSituacao(
    membro: Member,
    textoAcao: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(38.dp),
                tint = Color.Black
            )

            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text(membro.nome, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    "${membro.cargo} • ${membro.setor}",
                    fontSize = 10.sp,
                    color = Color.DarkGray
                )
            }

            Text(
                textoAcao,
                color = if (textoAcao == "Ativar") {
                    Color(0xFF287A43)
                } else {
                    Color(0xFFB3261E)
                },
                fontSize = 11.sp
            )
        }
    }
}