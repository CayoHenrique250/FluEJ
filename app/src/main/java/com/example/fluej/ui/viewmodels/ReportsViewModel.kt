package com.example.fluej.ui.viewmodels

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.ActivityRepository
import com.example.fluej.data.Member
import com.example.fluej.data.MemberRepository
import com.example.fluej.data.Periodo
import com.example.fluej.data.Relatorio
import com.example.fluej.data.RelatorioBuilder
import com.example.fluej.data.ReportDates
import com.example.fluej.data.XlsxWriter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun activityReportsViewModel(): ReportsViewModel {
    var context: Context = LocalContext.current
    while (context is ContextWrapper && context !is ComponentActivity) {
        context = context.baseContext
    }
    return viewModel(viewModelStoreOwner = context as ComponentActivity)
}

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val memberRepository = MemberRepository(db.memberDao())
    private val atividadeRepository = ActivityRepository(db.activityDao())

    var periodo by mutableStateOf(Periodo.SEMANAL)
        private set
    var inicio by mutableStateOf("")
        private set
    var fim by mutableStateOf("")
        private set

    var cargo by mutableStateOf<String?>(null)
        private set

    var membroId by mutableStateOf<String?>(null)
        private set

    var todosMembros by mutableStateOf<List<Member>>(emptyList())
        private set
    var relatorio by mutableStateOf<Relatorio?>(null)
        private set
    var carregando by mutableStateOf(false)
        private set
    var mensagem by mutableStateOf<String?>(null)
        private set

    val membrosFiltrados: List<Member>
        get() = todosMembros
            .filter { cargo == null || it.cargo == cargo }
            .sortedBy { it.nome.lowercase() }

    private var jobRecalculo: Job? = null

    init {
        val (i, f) = ReportDates.intervalo(Periodo.SEMANAL)
        inicio = i
        fim = f
    }

    fun atualizar() {
        viewModelScope.launch {
            carregando = true
            try {
                carregarLocal()
                sincronizar()
                carregarLocal()
            } finally {
                carregando = false
            }
        }
    }

    private suspend fun sincronizar() {
        memberRepository.sincronizarPendentes()
        atividadeRepository.sincronizarPendentes()
        memberRepository.baixarDoSupabase()
        atividadeRepository.baixarDoSupabase()
    }

    private suspend fun carregarLocal() {
        todosMembros = memberRepository.getMembrosLocais()
        if (membroId != null && todosMembros.none { it.id == membroId }) membroId = null
        recalcular()
    }

    private suspend fun recalcular() {
        val membros = todosMembros.filter {
            (cargo == null || it.cargo == cargo) && (membroId == null || it.id == membroId)
        }
        val atividades = atividadeRepository.getPorPeriodo(inicio, fim)
        relatorio = RelatorioBuilder.construir(inicio, fim, membros, atividades)
    }

    private fun recalcularAsync() {
        jobRecalculo?.cancel()
        jobRecalculo = viewModelScope.launch { recalcular() }
    }

    fun selecionarPeriodo(novo: Periodo) {
        periodo = novo
        val (i, f) = ReportDates.intervalo(novo)
        inicio = i
        fim = f
        recalcularAsync()
    }

    fun definirIntervalo(novoInicio: String, novoFim: String) {
        inicio = novoInicio
        fim = novoFim
        recalcularAsync()
    }

    fun selecionarCargo(novo: String?) {
        cargo = novo
        val membroAindaValido = todosMembros.any {
            it.id == membroId && (novo == null || it.cargo == novo)
        }
        if (!membroAindaValido) membroId = null
        recalcularAsync()
    }

    fun selecionarMembro(id: String?) {
        membroId = id
        recalcularAsync()
    }

    fun nomeArquivo(): String = "relatorio_${inicio}_a_${fim}.xlsx"

    fun exportar(uri: Uri) {
        val rel = relatorio ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    getApplication<Application>().contentResolver.openOutputStream(uri)
                        ?.use { XlsxWriter.escrever(rel.tabelaExcel(), it) }
                        ?: throw IOException("Não foi possível abrir o arquivo")
                }
                mensagem = "Relatório salvo com sucesso!"
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("Relatorio", "Falha ao exportar: ${e.message}", e)
                mensagem = "Erro ao salvar o relatório: ${e.message}"
            }
        }
    }

    fun limparMensagem() {
        mensagem = null
    }
}