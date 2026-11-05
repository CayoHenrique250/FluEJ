package com.example.fluej.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificacaoDto(
    val id: String,
    val tipo: String,           
    val titulo: String,
    val mensagem: String = "",
    val data: String = ""      
)

@Serializable
data class NotificacoesDto(
    val total: Int = 0,
    val itens: List<NotificacaoDto> = emptyList()
)

@Serializable
data class DocumentoPendenteDto(
    val id: String,
    val setor: String,
    val titulo: String,
    val quantidade: Int = 0
)

@Serializable
data class FeriadoDto(
    val data: String,
    val nome: String,
    val tipo: String = "national",
    @SerialName("faltam_dias") val faltamDias: Int? = null
)

@Serializable
data class RankingItemDto(
    @SerialName("membro_id") val membroId: String,
    val nome: String,
    val cargo: String,
    val setor: String,
    val pontos: Int,
    val horas: Double
)

@Serializable
data class RankingDto(
    val setor: String? = null,
    val inicio: String,
    @SerialName("fim_exclusivo") val fimExclusivo: String,
    val ordem: String,
    val itens: List<RankingItemDto> = emptyList()
)

@Serializable
data class PainelSetorDto(
    val setor: String,
    val membros: Int,
    val pontos: Int,
    val horas: Double
)

@Serializable
data class PainelDto(
    val setor: String? = null,
    @SerialName("membros_ativos") val membrosAtivos: Int,
    val projetos: Int,
    @SerialName("pontos_no_mes") val pontosNoMes: Int,
    @SerialName("horas_no_mes") val horasNoMes: Double,
    @SerialName("por_setor") val porSetor: List<PainelSetorDto> = emptyList()
)