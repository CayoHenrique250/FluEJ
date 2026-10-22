package com.example.fluej.data

import android.util.Log
import androidx.room.withTransaction
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException

class ProjectRepository(private val db: AppDatabase) {

    private val dao = db.projectDao()

    suspend fun adicionarProjeto(project: Project, memberIds: List<String>) {
        val vinculos = memberIds.map { ProjectMember(projectId = project.id, memberId = it) }
        db.withTransaction {
            dao.insertProject(project)
            dao.insertVinculos(vinculos)
        }
        sincronizarPendentes()
    }

    suspend fun contarPorSetor(setor: String): Int = dao.contarPorSetor(setor)

    suspend fun sincronizarPendentes() {
        dao.getProjetosNaoSincronizados().forEach { projeto ->
            try {
                supabase.postgrest["projetos"].upsert(projeto)
                dao.updateProject(projeto.copy(isSynced = true))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("SupabaseErro", "Falha ao enviar projeto ${projeto.nome}: ${e.message}", e)
            }
        }
        dao.getVinculosNaoSincronizados().forEach { vinculo ->
            try {
                supabase.postgrest["projeto_membros"].upsert(vinculo)
                dao.updateVinculo(vinculo.copy(isSynced = true))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("SupabaseErro", "Falha ao enviar vínculo de projeto: ${e.message}", e)
            }
        }
    }

    suspend fun baixarDoSupabase() {
        try {
            val projetos = supabase.postgrest["projetos"].select().decodeList<Project>()
            val projetosPendentes = dao.getProjetosNaoSincronizados().map { it.id }.toSet()
            dao.insertProjects(
                projetos.filter { it.id !in projetosPendentes }.map { it.copy(isSynced = true) }
            )

            val vinculos = supabase.postgrest["projeto_membros"].select().decodeList<ProjectMember>()
            val vinculosPendentes = dao.getVinculosNaoSincronizados()
                .map { it.projectId to it.memberId }.toSet()
            dao.insertVinculos(
                vinculos.filter { (it.projectId to it.memberId) !in vinculosPendentes }
                    .map { it.copy(isSynced = true) }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SupabaseErro", "Falha ao baixar projetos: ${e.message}", e)
        }
    }
}