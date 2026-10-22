package com.example.fluej.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Entity(tableName = "projetos")
@Serializable
data class Project(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val descricao: String = "",
    @SerialName("total_pontos")
    val totalPontos: Int = 0,
    val setor: String,
    @Transient
    val isSynced: Boolean = false
)

@Entity(tableName = "projeto_membros", primaryKeys = ["projectId", "memberId"])
@Serializable
data class ProjectMember(
    @SerialName("project_id")
    val projectId: String,
    @SerialName("member_id")
    val memberId: String,
    @Transient
    val isSynced: Boolean = false
)