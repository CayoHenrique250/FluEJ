package com.example.fluej.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Perfil(val codigo: String) {
    PRESIDENTE("presidente"),
    RH("rh"),
    PROJETOS("projetos"),
    COMERCIAL("comercial"),
    FINANCEIRO("financeiro"),
    ORIENTADOR("orientador");

    companion object {
        fun deCodigo(codigo: String?): Perfil? =
            values().firstOrNull { it.codigo == codigo?.trim()?.lowercase() }
    }
}

@Serializable
data class PerfilUsuario(
    @SerialName("user_id") val userId: String,
    val perfil: String,
    val setor: String? = null,
    val nome: String? = null
)