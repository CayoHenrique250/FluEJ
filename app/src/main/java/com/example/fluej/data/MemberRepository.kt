package com.example.fluej.data

import android.util.Log
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException

class MemberRepository(private val memberDao: MemberDao) {

    suspend fun getMembrosLocais(): List<Member> = memberDao.getAllMembers()

    suspend fun adicionarMembro(member: Member) {
        memberDao.insertMember(member)
        enviarParaSupabase(member)
    }

    suspend fun sincronizarPendentes() {
        memberDao.getMembrosNaoSincronizados().forEach { enviarParaSupabase(it) }
    }

    suspend fun baixarDoSupabase() {
        try {
            val remotos = supabase.postgrest["members"].select().decodeList<Member>()
            val pendentes = memberDao.getMembrosNaoSincronizados().map { it.id }.toSet()
            memberDao.insertAll(
                remotos.filter { it.id !in pendentes }.map { it.copy(isSynced = true) }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SupabaseErro", "Falha ao baixar membros: ${e.message}", e)
        }
    }

    private suspend fun enviarParaSupabase(member: Member) {
        try {
            supabase.postgrest["members"].upsert(member)
            memberDao.updateMember(member.copy(isSynced = true))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SupabaseErro", "Falha ao enviar ${member.nome}: ${e.message}", e)
        }
    }
}