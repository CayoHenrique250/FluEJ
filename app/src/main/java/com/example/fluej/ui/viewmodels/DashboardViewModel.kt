package com.example.fluej.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.ActivityRepository
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.MemberRepository
import com.example.fluej.data.Periodo
import com.example.fluej.data.ProjectRepository
import com.example.fluej.data.ReportDates
import com.example.fluej.data.Session
import kotlinx.coroutines.launch

data class RankingItem(val nome: String, val pontos: Int)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val memberRepository = MemberRepository(db.memberDao())
    private val activityRepository = ActivityRepository(db.activityDao())
    private val projectRepository = ProjectRepository(db)

    var totalProjetos by mutableStateOf(0)
        private set

    val setor: String = Session.departmentOfManager

    var totalMembros by mutableStateOf(0)
        private set
    var totalPontos by mutableStateOf(0)
        private set
    var totalHoras by mutableStateOf(0.0)
        private set

    var ranking by mutableStateOf<List<RankingItem>>(emptyList())
        private set
    var carregando by mutableStateOf(false)
        private set

    fun atualizar() {
        viewModelScope.launch {
            carregando = true
            try {
                carregar()
                memberRepository.sincronizarPendentes()
                activityRepository.sincronizarPendentes()
                memberRepository.baixarDoSupabase()
                activityRepository.baixarDoSupabase()
                projectRepository.baixarDoSupabase()
                projectRepository.sincronizarPendentes()
                carregar()
            } finally {
                carregando = false
            }
        }
    }

    private suspend fun carregar() {
        val (inicio, fim) = ReportDates.intervalo(Periodo.MENSAL)

        val membros = memberRepository.getMembrosLocais().filter { it.setor == setor }
        val ids = membros.map { it.id }.toSet()

        val pontosPorMembro = activityRepository.getPorPeriodo(inicio, fim)
            .filter { it.memberId in ids }
            .groupBy { it.memberId }
            .mapValues { (_, lista) -> lista.sumOf { it.pontos } }

        ranking = membros
            .map { RankingItem(it.nome, pontosPorMembro[it.id] ?: 0) }
            .sortedWith(compareBy<RankingItem> { it.pontos }.thenBy { it.nome.lowercase() })

        totalMembros = membros.size
        totalPontos = ranking.sumOf { it.pontos }
        totalHoras = totalPontos / 60.0
        totalProjetos = projectRepository.contarPorSetor(setor)
    }
}