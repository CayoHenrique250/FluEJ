
package com.example.fluej.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.Activity
import com.example.fluej.data.ActivityRepository
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.Member
import com.example.fluej.data.MemberRepository
import com.example.fluej.data.Project
import com.example.fluej.data.ProjectRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.time.LocalDate

class PontosViewModel(application: Application) :
    AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)

    private val memberRepository = MemberRepository(db.memberDao())
    private val projectRepository = ProjectRepository(db)
    private val activityRepository = ActivityRepository(db.activityDao())

    var membros by mutableStateOf<List<Member>>(emptyList())
        private set

    var projetos by mutableStateOf<List<Project>>(emptyList())
        private set

    var membroSelecionado by mutableStateOf<Member?>(null)
        private set

    var projetoSelecionado by mutableStateOf<Project?>(null)
        private set

    var carregando by mutableStateOf(false)
        private set

    var salvando by mutableStateOf(false)
        private set

    var mensagem by mutableStateOf<String?>(null)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    fun carregarDados() {
        if (carregando) return

        viewModelScope.launch {
            carregando = true
            erro = null

            try {
                memberRepository.sincronizarPendentes()
                projectRepository.sincronizarPendentes()
                activityRepository.sincronizarPendentes()

                memberRepository.baixarDoSupabase()
                projectRepository.baixarDoSupabase()

                membros = memberRepository.getMembrosLocais()
                    .filter { it.ativo }
                    .sortedBy { it.nome.lowercase() }

                projetos = projectRepository.getProjetosLocais()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                erro = "Não foi possível carregar os dados."
            } finally {
                carregando = false
            }
        }
    }

    fun selecionarMembro(member: Member) {
        membroSelecionado = member
        projetoSelecionado = null
        mensagem = null
        erro = null

        viewModelScope.launch {
            try {
                projetos = projectRepository.getProjetosDoMembro(member.id)
            } catch (e: Exception) {
                erro = "Não foi possível carregar os projetos do membro."
            }
        }
    }

    fun selecionarProjeto(project: Project) {
        projetoSelecionado = project
        mensagem = null
        erro = null
    }

    fun salvarPontos(
        descricao: String,
        quantidade: Int,
        onConcluido: () -> Unit = {}
    ) {
        if (salvando) return

        val membro = membroSelecionado
        val projeto = projetoSelecionado

        if (membro == null) {
            erro = "Selecione um membro."
            return
        }

        if (projeto == null) {
            erro = "Selecione um projeto."
            return
        }

        if (descricao.isBlank()) {
            erro = "Informe a descrição do lançamento."
            return
        }

        if (quantidade <= 0) {
            erro = "Informe uma quantidade de pontos válida."
            return
        }

        viewModelScope.launch {
            salvando = true
            erro = null
            mensagem = null

            try {
                val atividade = Activity(
                    memberId = membro.id,
                    projectId = projeto.id,
                    data = LocalDate.now().toString(),
                    pontos = quantidade,
                    descricao = descricao.trim()
                )

                val sincronizado =
                    activityRepository.adicionarAtividade(atividade)

                mensagem = if (sincronizado) {
                    "Pontos registrados e sincronizados com sucesso!"
                } else {
                    "Pontos salvos localmente. A sincronização ficará pendente."
                }

                onConcluido()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                erro = "Não foi possível registrar os pontos."
            } finally {
                salvando = false
            }
        }
    }

    fun limparMensagem() {
        mensagem = null
        erro = null
    }
}
