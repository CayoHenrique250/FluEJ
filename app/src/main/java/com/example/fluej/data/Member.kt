package com.example.fluej.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.UUID

@Entity(tableName = "members")
@Serializable
data class Member(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val cpf: String,
    @SerialName("data_nascimento")
    val dataNascimento: String,
    val email: String,
    val telefone: String,
    val cargo: String,
    val setor: String,
    @Transient
    val isSynced: Boolean = false
)