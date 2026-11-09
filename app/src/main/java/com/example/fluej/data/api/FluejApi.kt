package com.example.fluej.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FluejApi {

    @GET("v1/notificacoes")
    suspend fun notificacoes(): NotificacoesDto

    @GET("v1/documentos-pendentes")
    suspend fun documentosPendentes(): List<DocumentoPendenteDto>

    @GET("v1/feriados/proximos")
    suspend fun feriadosProximos(@Query("dias") dias: Int = 30): List<FeriadoDto>

    @GET("v1/feriados/{ano}")
    suspend fun feriados(@Path("ano") ano: Int): List<FeriadoDto>

    @GET("v1/painel")
    suspend fun painel(): PainelDto

    @GET("v1/ranking")
    suspend fun ranking(@Query("ordem") ordem: String = "asc"): RankingDto

    @GET("v1/membros/{membro_id}/desempenho")
    suspend fun buscarDesempenho(
        @Path("membro_id") membroId: String,
        @Query("meses") meses: Int = 6
    ): DesempenhoMembro
}