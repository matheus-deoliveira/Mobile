package com.example.pokedroid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokedroid.data.model.PokemonResult
import com.example.pokedroid.data.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class PokemonViewModel(repository: PokemonRepository) : ViewModel() {

    // Expõe um Flow de dados paginados que a UI pode coletar.
    // cachedIn() garante que os dados sobrevivam a mudanças de configuração (como rotação da tela)
    // e que o fluxo de dados seja compartilhado entre observadores.
    val pokemonPagingData: Flow<PagingData<PokemonResult>> = repository.getPokemonStream().cachedIn(viewModelScope)

    // A lógica de favoritos será adicionada aqui em um próximo passo.
}

// O ViewModel agora precisa do repositório no seu construtor.
// Para que o Android saiba como criar um ViewModel assim, precisamos de uma Factory.
class PokemonViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}