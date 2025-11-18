package com.example.appclima.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SearchScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val _cidadeEncontrada = MutableLiveData<String>()
    val cidadeEncontrada: LiveData<String> get() = _cidadeEncontrada

    private val _mensagemErro = MutableLiveData<String>()
    val mensagemErro: LiveData<String> get() = _mensagemErro

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun validarCidade(nomeCidade: String) {
        if (nomeCidade.trim().isEmpty()) {
            _mensagemErro.value = "Por favor, digite o nome de uma cidade."
            return
        } else {
            // Se validou, avisa a view que está tudo certo
            _cidadeEncontrada.value = nomeCidade
        }
    }
}