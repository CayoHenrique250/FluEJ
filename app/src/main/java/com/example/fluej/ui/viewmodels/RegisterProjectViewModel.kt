package com.example.fluej.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.Member
import com.example.fluej.data.MemberRepository
import com.example.fluej.data.Project
import com.example.fluej.data.ProjectRepository
import com.example.fluej.data.Session
import kotlinx.coroutines.launch

class RegisterProjectViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val memberRepository = MemberRepository(db.memberDao())
    private val projectRepository = ProjectRepository(db)

    val setor: String = Session.departmentOfManager

    var nome by mutableStateOf("")
    var descricao by mutableStateOf("")
    var totalPontos by mutableStateOf("")

    var membrosDoSetor by mutableStateOf<List<Member>>(emptyList())
        private set

    var selecionados by mutableStateOf<Set<String>>(emptySet())
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    var salvando by mutableStateOf(false)
        private set

    val nomesSelecionados: String
        get() = membrosDoSetor
            .filter { it.id in selecionados }
            .joinToString(", ") { it.nome }

    init {
        viewModelScope.launch {
            carregarMembros()
            memberRepository.baixarDoSupabase() 
            carregarMembros()
        }
    }

    private suspend fun carregarMembros() {
        membrosDoSetor = memberRepository.getMembrosLocais()
            .filter { it.setor == setor && it.ativo }
            .sortedBy { it.nome.lowercase() }
        selecionados = selecionados.filter { id -> membrosDoSetor.any { it.id == id } }.toSet()
    }

    fun definirSelecionados(ids: Set<String>) {
        selecionados = ids
    }

    fun salvar(onSuccess: () -> Unit) {
        if (salvando) return

        if (nome.isBlank()) { erro = "Informe o nome do projeto."; return }
        if (selecionados.isEmpty()) { erro = "Selecione ao menos um membro."; return }
        val pontos = totalPontos.toIntOrNull() ?: run {
            erro = "Informe o total de pontos."
            return
        }
        erro = null

        salvando = true
        viewModelScope.launch {
            try {
                projectRepository.adicionarProjeto(
                    project = Project(
                        nome = nome.trim(),
                        descricao = descricao.trim(),
                        totalPontos = pontos,
                        setor = setor
                    ),
                    memberIds = selecionados.toList()
                )
                onSuccess()
            } finally {
                salvando = false
            }
        }
    }
}