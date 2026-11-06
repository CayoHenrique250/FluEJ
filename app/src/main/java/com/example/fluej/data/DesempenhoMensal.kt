package com.example.fluej.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "desempenho_mensal")
data class DesempenhoMensal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val mes: String,
    val ordem: Int,
    val pontos: Int,
    val horas: Double,
    val membrosAtivos: Int,
    val projetosAtivos: Int
)