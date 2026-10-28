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
import com.example.fluej.data.Perfil
import com.example.fluej.data.Session
import com.example.fluej.ui.membros.Membro
import kotlinx.coroutines.launch

class MembrosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val memberRepository = MemberRepository(db.memberDao())
    private val activityRepository = ActivityRepository(db.activityDao())

    val escopoPorSetor: Boolean = Session.perfil in setOf(
        Perfil.PROJETOS, Perfil.COMERCIAL, Perfil.FINANCEIRO
    )
    private val setorDoEscopo: String = Session.departmentOfManager

    val podeGerenciar: Boolean = Session.perfil == Perfil.RH || Session.perfil == Perfil.PRESIDENTE

    var membros by mutableStateOf<List<Membro>>(emptyList())
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
                carregar()
            } finally {
                carregando = false
            }
        }
    }

    private suspend fun carregar() {
        val todos = memberRepository.getMembrosLocais()
        val visiveis = if (escopoPorSetor) todos.filter { it.setor == setorDoEscopo } else todos

        val pontosPorMembro = activityRepository.getPorPeriodo("0000-01-01", "9999-12-31")
            .groupBy { it.memberId }
            .mapValues { (_, lista) -> lista.sumOf { it.pontos } }

        membros = visiveis
            .map {
                Membro(
                    id = it.id,
                    nome = it.nome,
                    cargo = "${it.cargo} · ${it.setor}",
                    pontos = pontosPorMembro[it.id] ?: 0,
                    ativo = it.ativo,
                    setor = it.setor
                )
            }
            .sortedBy { it.nome.lowercase() }
    }

    fun alternarAtivo(membro: Membro) {
        if (!podeGerenciar) return
        viewModelScope.launch {
            val atual = memberRepository.getMembrosLocais().firstOrNull { it.id == membro.id }
                ?: return@launch
            memberRepository.definirAtivo(atual, !atual.ativo)
            carregar()
        }
    }
}