package com.example.fluej.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluej.data.AppDatabase
import com.example.fluej.data.Member
import com.example.fluej.data.MemberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterMemberViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).memberDao()
    private val repository = MemberRepository(dao)

    var nome by mutableStateOf("")
    var cpf by mutableStateOf("")
    var dataNascimento by mutableStateOf("")
    var email by mutableStateOf("")
    var telefone by mutableStateOf("")
    var cargo by mutableStateOf("")
    var setor by mutableStateOf("")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.sincronizarPendentes()
        }
    }

    fun saveMember(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val novoMembro = Member(
                nome = nome,
                cpf = cpf,
                dataNascimento = dataNascimento,
                email = email,
                telefone = telefone,
                cargo = cargo,
                setor = setor
            )

            repository.adicionarMembro(novoMembro)

            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}