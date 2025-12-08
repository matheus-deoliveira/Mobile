package com.example.pokedroid.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pokedroid.data.model.PokemonResult
import com.example.pokedroid.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class SearchResultViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _searchResults = MutableLiveData<List<PokemonResult>>()
    val searchResults: LiveData<List<PokemonResult>> = _searchResults

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun searchPokemon(query: String) {
        viewModelScope.launch {
            try {
                val allPokemons = repository.getAllPokemons().results
                val filteredList = allPokemons.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                _searchResults.postValue(filteredList)
            } catch (e: Exception) {
                _error.postValue("Falha ao buscar Pokémon: ${e.message}")
            }
        }
    }
}

class SearchResultViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchResultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchResultViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}