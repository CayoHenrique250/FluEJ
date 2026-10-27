package com.example.fluej.ui.membros

data class Membro(
    val nome: String,
    val cargo: String,
    val pontos: Int,
    val ativo: Boolean,
    val id: String = "",
    val setor: String = ""
)