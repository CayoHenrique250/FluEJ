package com.example.fluej.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.DesempenhoMensal
import com.example.fluej.data.sampleDataDesempenho
import kotlinx.coroutines.launch
import android.util.Log
import com.example.fluej.data.ActivityRepository
import com.example.fluej.data.MemberRepository
import com.example.fluej.data.Periodo
import com.example.fluej.data.ReportDates
import kotlinx.coroutines.CancellationException

class DashboardOrientadorViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).desempenhoDao()
    private val memberRepository = MemberRepository(AppDatabase.getDatabase(application).memberDao())
    private val activityRepository = ActivityRepository(AppDatabase.getDatabase(application).activityDao())

    var ranking by mutableStateOf<List<RankingItem>>(emptyList())
        private set

    var desempenho by mutableStateOf<List<DesempenhoMensal>>(emptyList())
        private set

    var totalMembros by mutableStateOf(0)
        private set

    var totalProjetos by mutableStateOf(0)
        private set

    var totalPontos by mutableStateOf(0)
        private set

    var totalHoras by mutableStateOf(0.0)
        private set

    var carregando by mutableStateOf(false)
        private set

    fun atualizar() {
        viewModelScope.launch {
            carregando = true
            try {
                if (dao.count() == 0) {
                    dao.insertAll(sampleDataDesempenho)
                }
                val registros = dao.findAll()
                desempenho = registros
                totalPontos = registros.sumOf { it.pontos }
                totalHoras = registros.sumOf { it.horas }
                totalMembros = registros.lastOrNull()?.membrosAtivos ?: 0
                totalProjetos = registros.lastOrNull()?.projetosAtivos ?: 0
                carregarRanking()
                try {
                    memberRepository.baixarDoSupabase()
                    activityRepository.baixarDoSupabase()
                    carregarRanking()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("ApiErro", "Ranking: ${e.message}", e)
                }
            }

            finally {
                carregando = false
            }
        }
    }
    private suspend fun carregarRanking() {
        val (inicio, fim) = ReportDates.intervalo(Periodo.MENSAL)

        val membros = memberRepository.getMembrosLocais().filter { it.ativo }

        val pontosPorMembro = activityRepository.getPorPeriodo(inicio, fim)
            .groupBy { it.memberId }
            .mapValues { (_, lista) -> lista.sumOf { it.pontos } }

        ranking = membros
            .map { RankingItem(it.nome, pontosPorMembro[it.id] ?: 0) }
            .sortedWith(compareByDescending<RankingItem> { it.pontos }.thenBy { it.nome.lowercase() })
    }
}