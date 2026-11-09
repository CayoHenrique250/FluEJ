package com.example.fluej.data.pontos

import com.example.fluej.data.supabase
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.result.decodeAs
import io.github.jan.supabase.postgrest.result.decodeList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.result.decodeAs

@Serializable
data class PontosPassivosCargo(
    val id: Long,
    val cargo: String,

    @SerialName("pontos_semanais")
    val pontosSemanais: Int
)

class PontosPassivosRepository {

    suspend fun buscarCargos(): List<PontosPassivosCargo> {
        return supabase
            .from("pontos_passivos_cargos")
            .select()
            .decodeList<PontosPassivosCargo>()
            .sortedBy { it.id }
    }

    suspend fun salvarPontos(
        cargo: String,
        pontos: Int
    ) {
        require(pontos >= 0) {
            "A pontuação não pode ser negativa."
        }

        supabase
            .from("pontos_passivos_cargos")
            .update({
                set("pontos_semanais", pontos)
            }) {
                filter {
                    eq("cargo", cargo)
                }
            }
    }

    suspend fun salvarTodos(
        cargos: List<PontosPassivosCargo>
    ) {
        cargos.forEach { item ->
            salvarPontos(
                cargo = item.cargo,
                pontos = item.pontosSemanais
            )
        }
    }

    suspend fun creditarPontosSemanais(): Int {
        return supabase
            .postgrest
            .rpc("lancar_pontos_passivos_semana")
            .decodeAs<Int>()
    }
}