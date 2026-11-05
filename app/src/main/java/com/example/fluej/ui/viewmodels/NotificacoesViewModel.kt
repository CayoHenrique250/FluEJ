package com.example.fluej.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.api.ApiRepository
import com.example.fluej.data.api.NotificacaoDto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class NotificacoesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ApiRepository(AppDatabase.getDatabase(application).apiCacheDao())

    var itens by mutableStateOf<List<NotificacaoDto>>(emptyList())
        private set
    var carregando by mutableStateOf(false)
        private set
    var offline by mutableStateOf(false)
        private set
    var erro by mutableStateOf<String?>(null)
        private set

    fun atualizar() {
        viewModelScope.launch {
            carregando = true
            try {
                val resultado = repository.notificacoes()
                itens = resultado.dados.itens
                offline = resultado.deCache
                erro = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("ApiErro", "Notificações: ${e.message}", e)
                erro = "Não foi possível carregar as notificações."
            } finally {
                carregando = false
            }
        }
    }
}