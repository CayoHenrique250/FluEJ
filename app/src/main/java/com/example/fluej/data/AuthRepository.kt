package com.example.fluej.data

import android.util.Log
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.CancellationException

sealed class ResultadoLogin {
    data class Sucesso(val perfil: Perfil) : ResultadoLogin()
    data class Erro(val mensagem: String) : ResultadoLogin()
}

object AuthRepository {

    suspend fun entrar(emailDigitado: String, senhaDigitada: String): ResultadoLogin {
       
        try {
            supabase.auth.signInWith(Email) {
                email = emailDigitado
                password = senhaDigitada
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: RestException) {
            Log.e("AuthErro", "Login recusado: ${e.message}", e)
            return ResultadoLogin.Erro("E-mail ou senha inválidos.")
        } catch (e: Exception) {
            Log.e("AuthErro", "Falha de conexão no login: ${e.message}", e)
            return ResultadoLogin.Erro("Não foi possível conectar. Verifique sua internet.")
        }

        return try {
            val userId = supabase.auth.currentUserOrNull()?.id
            val registro = if (userId == null) null else {
                supabase.postgrest["perfis"]
                    .select { filter { eq("user_id", userId) } }
                    .decodeSingleOrNull<PerfilUsuario>()
            }
            val perfil = Perfil.deCodigo(registro?.perfil)

            if (registro == null || perfil == null) {
                sair()
                ResultadoLogin.Erro("Seu usuário não tem um perfil de acesso configurado.")
            } else {
                Session.iniciar(perfil, registro.setor, registro.nome)
                ResultadoLogin.Sucesso(perfil)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("AuthErro", "Falha ao carregar perfil: ${e.message}", e)
            sair()
            ResultadoLogin.Erro("Não foi possível carregar seu perfil de acesso.")
        }
    }

    suspend fun sair() {
        try {
            supabase.auth.signOut()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("AuthErro", "Falha ao encerrar sessão no servidor: ${e.message}", e)
            try { supabase.auth.clearSession() } catch (_: Exception) { }
        }
        Session.limpar()
    }
}