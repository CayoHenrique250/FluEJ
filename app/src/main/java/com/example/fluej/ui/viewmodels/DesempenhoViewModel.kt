package com.example.fluej.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.model.DesempenhoMembro
import com.example.fluej.data.repository.DesempenhoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DesempenhoViewModel : ViewModel() {

    private val repository = DesempenhoRepository()

    var desempenho by mutableStateOf<DesempenhoMembro?>(null)
        private set

    var carregando by mutableStateOf(false)
        private set

    var erro by mutableStateOf<String?>(null)
        private set

    fun carregar(membroId: String, meses: Int = 6) {

        viewModelScope.launch {

            carregando = true
            erro = null
            desempenho = null

            try {
                desempenho = repository.buscarDesempenho(
                    membroId = membroId,
                    meses = meses
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: HttpException) {

                erro = when (e.code()) {
                    401 -> "Sua sessão expirou. Entre novamente."
                    403 -> "Você não possui permissão para consultar este membro."
                    404 -> "Membro não encontrado."
                    500, 502 -> "O servidor não conseguiu concluir a consulta."
                    else -> "Erro ao consultar desempenho (${e.code()})."
                }

            } catch (e: IOException) {
                erro = "Falha de conexão. Verifique sua internet."

            } catch (e: Exception) {
                erro = e.message ?: "Não foi possível carregar o desempenho."
            } finally {
                carregando = false
            }
        }
    }
}