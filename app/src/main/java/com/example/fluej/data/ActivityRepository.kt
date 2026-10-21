package com.example.fluej.data

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException

class ActivityRepository(private val dao: ActivityDao) {

    suspend fun getPorPeriodo(inicio: String, fim: String): List<Activity> =
        dao.getPorPeriodo(inicio, fim)

    suspend fun adicionarAtividade(atividade: Activity) {
        dao.insertActivity(atividade)
        enviar(atividade)
    }

    suspend fun sincronizarPendentes() {
        dao.getNaoSincronizadas().forEach { enviar(it) }
    }

    suspend fun baixarDoSupabase() {
        try {
            val remotas = supabase.postgrest["atividades"].select().decodeList<Activity>()
            val pendentes = dao.getNaoSincronizadas().map { it.id }.toSet()
            dao.insertAll(
                remotas.filter { it.id !in pendentes }.map { it.copy(isSynced = true) }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SupabaseErro", "Falha ao baixar atividades: ${e.message}", e)
        }
    }

    private suspend fun enviar(atividade: Activity) {
        try {
            supabase.postgrest["atividades"].upsert(atividade)
            dao.updateActivity(atividade.copy(isSynced = true))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SupabaseErro", "Falha ao enviar atividade: ${e.message}", e)
        }
    }
}