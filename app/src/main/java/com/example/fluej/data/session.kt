package com.example.fluej.data

object Session {
    var perfil: Perfil? = null
        private set

    var departmentOfManager: String = ""
        private set

    var nomeUsuario: String = ""
        private set

    val autenticado: Boolean get() = perfil != null

    fun iniciar(perfil: Perfil, setor: String?, nome: String?) {
        this.perfil = perfil
        departmentOfManager = setor ?: if (perfil == Perfil.RH) "RH" else ""
        nomeUsuario = nome.orEmpty()
    }

    fun limpar() {
        perfil = null
        departmentOfManager = ""
        nomeUsuario = ""
    }
}