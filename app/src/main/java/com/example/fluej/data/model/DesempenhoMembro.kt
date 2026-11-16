package com.example.fluej.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DesempenhoMembro(
    val membro: MembroResumo,
    @SerialName("total_pontos") val totalPontos: Int = 0,
    @SerialName("total_horas") val totalHoras: Double = 0.0,
    @SerialName("pontos_por_mes") val pontosPorMes: List<PontosPorMes> = emptyList(),
    val projetos: List<ProjetoDesempenho> = emptyList()
)

@Serializable
data class MembroResumo(
    val nome: String,
    val cargo: String = "",
    val setor: String = "",
    val ativo: Boolean = true
)

@Serializable
data class PontosPorMes(
    val mes: String,
    val pontos: Int = 0,
    val horas: Double = 0.0
)

@Serializable
data class ProjetoDesempenho(
    val nome: String,
    @SerialName("total_pontos") val totalPontos: Int = 0
)
