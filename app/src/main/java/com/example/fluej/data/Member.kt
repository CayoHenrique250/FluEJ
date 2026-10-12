package com.example.fluej.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val cpf: String,
    val dataNascimento: String,
    val email: String,
    val telefone: String,
    val cargo: String,
    val setor: String
)