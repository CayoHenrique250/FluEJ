package com.example.fluej.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.DesempenhoMensal
import com.example.fluej.data.api.ApiRepository
import com.example.fluej.data.api.RankingItemDto
import com.example.fluej.data.sampleDataDesempenho
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class DashboardPresidenteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).desempenhoDao()
    private val apiRepository = ApiRepository(AppDatabase.getDatabase(application).apiCacheDao())

    var desempenho by mutableStateOf<List<DesempenhoMensal>>(emptyList())
        private set

    var totalProjetos by mutableStateOf(0)
        private set

    var totalMembros by mutableStateOf(0)
        private set

    var totalPontos by mutableStateOf(0)
        private set

    var totalHoras by mutableStateOf(0.0)
        private set

    var ranking by mutableStateOf<List<RankingItemDto>>(emptyList())
        private set

    var carregando by mutableStateOf(false)
        private set

    var usandoCache by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
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
                totalProjetos = registros.lastOrNull()?.projetosAtivos ?: 0

                val resultado = apiRepository.ranking("desc")
                val itens = resultado.dados.itens
                ranking = itens
                totalMembros = itens.size
                totalPontos = itens.sumOf { it.pontos }
                totalHoras = itens.sumOf { it.horas }
                usandoCache = resultado.deCache
                erro = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("ApiErro", "Dashboard presidente: ${e.message}", e)
                erro = "Não foi possível carregar o ranking."
            } finally {
                carregando = false
            }
        }
    }
}