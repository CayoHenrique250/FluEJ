package com.example.fluej.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.Member
import kotlinx.coroutines.launch

class RegisterMemberViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).memberDao()

    var nome by mutableStateOf("")
    var cpf by mutableStateOf("")
    var dataNascimento by mutableStateOf("")
    var email by mutableStateOf("")
    var telefone by mutableStateOf("")
    var cargo by mutableStateOf("")
    var setor by mutableStateOf("")

    fun saveMember(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val novoMembro = Member(
                nome = nome,
                cpf = cpf,
                dataNascimento = dataNascimento,
                email = email,
                telefone = telefone,
                cargo = cargo,
                setor = setor
            )
            dao.insertMember(novoMembro)
            onSuccess()
        }
    }
}