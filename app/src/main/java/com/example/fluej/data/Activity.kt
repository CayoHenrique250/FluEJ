package com.example.fluej.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Entity(tableName = "atividades")
@Serializable
data class Activity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @SerialName("member_id")
    val memberId: String,
    val data: String,
    val pontos: Int = 0,
    val descricao: String = "",
    @Transient
    val isSynced: Boolean = false
) {
    val horas: Double get() = pontos / 60.0
}