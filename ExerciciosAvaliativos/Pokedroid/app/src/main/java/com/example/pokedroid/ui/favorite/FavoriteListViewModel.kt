package com.example.pokedroid.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import com.example.pokedroid.domain.usecase.GetAllFavoritesUseCase
import com.example.pokedroid.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteListViewModel(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    // Converte o Flow do Room para um StateFlow observável na UI
    val favorites: StateFlow<List<FavoritePokemonEntity>> = getAllFavoritesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteFavorite(pokemon: FavoritePokemonEntity) {
        viewModelScope.launch {
            removeFavoriteUseCase(pokemon.id)
        }
    }
}

// Factory para criar o ViewModel (necessário pois temos argumentos no construtor)
class FavoriteListViewModelFactory(
    private val getAllFavoritesUseCase: GetAllFavoritesUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteListViewModel(getAllFavoritesUseCase, removeFavoriteUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}