package com.example.fluej.data.repository

import com.example.fluej.data.api.ApiClient
import com.example.fluej.data.model.DesempenhoMembro

class DesempenhoRepository {

private val api = ApiClient.api

suspend fun buscarDesempenho(
    membroId: String,
    meses: Int = 6
): DesempenhoMembro {

    require(meses in 1..24) {
        "O período deve estar entre 1 e 24 meses."
    }

    require(membroId.isNotBlank()) {
        "O identificador do membro não pode estar vazio."
    }

    return api.buscarDesempenho(
        membroId = membroId,
        meses = meses
    )
}

}