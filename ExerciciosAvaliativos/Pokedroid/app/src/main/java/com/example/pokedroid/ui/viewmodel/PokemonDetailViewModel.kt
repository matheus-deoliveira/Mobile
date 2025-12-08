package com.example.pokedroid.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pokedroid.data.model.PokemonDetailResponse
import com.example.pokedroid.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class PokemonDetailViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _pokemonDetail = MutableLiveData<PokemonDetailResponse?>()
    val pokemonDetail: LiveData<PokemonDetailResponse?> = _pokemonDetail

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getPokemonDetails(name: String) {
        viewModelScope.launch {
            try {
                val details = repository.getPokemonDetails(name)
                _pokemonDetail.postValue(details)
            } catch (e: Exception) {
                _error.postValue("Pokémon não encontrado!")
                _pokemonDetail.postValue(null) // Limpa qualquer detalhe anterior
            }
        }
    }
}

class PokemonDetailViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}