package com.example.fluej.data

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException

class ActivityRepository(private val dao: ActivityDao) {

    suspend fun getPorPeriodo(
        inicio: String,
        fim: String
    ): List<Activity> {
        return dao.getPorPeriodo(inicio, fim)
    }

    suspend fun adicionarAtividade(atividade: Activity): Boolean {
        dao.insertActivity(atividade)
        return enviar(atividade)
    }

    suspend fun sincronizarPendentes() {
        dao.getNaoSincronizadas().forEach { atividade ->
            enviar(atividade)
        }
    }

    suspend fun baixarDoSupabase() {
        try {
            val remotas = supabase
                .postgrest["atividades"]
                .select()
                .decodeList<Activity>()

            val pendentes = dao.getNaoSincronizadas()
                .map { it.id }
                .toSet()

            dao.insertAll(
                remotas
                    .filter { it.id !in pendentes }
                    .map { it.copy(isSynced = true) }
            )

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                "SupabaseErro",
                "Falha ao baixar atividades: ${e.message}",
                e
            )
        }
    }

    private suspend fun enviar(atividade: Activity): Boolean {
        return try {
            supabase
                .postgrest["atividades"]
                .upsert(atividade)

            dao.updateActivity(
                atividade.copy(isSynced = true)
            )

            true

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(
                "SupabaseErro",
                "Falha ao enviar atividade: ${e.message}",
                e
            )

            false
        }
    }
}