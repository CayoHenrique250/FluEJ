package com.example.fluej.data.api

import com.example.fluej.data.supabase
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.serializer

data class ApiResultado<T>(
    val dados: T,
    val deCache: Boolean,           
    val atualizadoEm: Long? = null
)

class ApiRepository(
    private val cacheDao: ApiCacheDao,
    private val api: FluejApi = ApiClient.api
) {
    private val json = ApiClient.json

    suspend fun notificacoes() = comCache("notificacoes") { api.notificacoes() }

    suspend fun documentosPendentes() = comCache("documentos") { api.documentosPendentes() }

    suspend fun painel() = comCache("painel") { api.painel() }

    suspend fun ranking(ordem: String = "asc") = comCache("ranking_$ordem") { api.ranking(ordem) }

    private suspend inline fun <reified T> comCache(
        chave: String,
        buscar: suspend () -> T
    ): ApiResultado<T> {
        val usuario = supabase.auth.currentUserOrNull()?.id.orEmpty()
        val chaveFinal = "$usuario:$chave"
        return try {
            val dados = buscar()
            cacheDao.salvar(
                ApiCache(chaveFinal, json.encodeToString(serializer<T>(), dados), System.currentTimeMillis())
            )
            ApiResultado(dados, deCache = false)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val salvo = cacheDao.buscar(chaveFinal) ?: throw e
            ApiResultado(
                dados = json.decodeFromString(serializer<T>(), salvo.json),
                deCache = true,
                atualizadoEm = salvo.atualizadoEm
            )
        }
    }
}