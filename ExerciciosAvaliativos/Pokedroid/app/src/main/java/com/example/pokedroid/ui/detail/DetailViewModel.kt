package com.example.pokedroid.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import com.example.pokedroid.domain.usecase.AddFavoriteUseCase
import com.example.pokedroid.domain.usecase.GetFavoriteStatusUseCase
import com.example.pokedroid.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val getFavoriteStatusUseCase: GetFavoriteStatusUseCase
    // ... outros use cases (getDetails)
) : ViewModel() {

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    // Objeto atual carregado na tela (precisamos dele para salvar)
    private var currentPokemonEntity: FavoritePokemonEntity? = null

    fun checkFavoriteStatus(id: Int) {
        viewModelScope.launch {
            _isFavorite.value = getFavoriteStatusUseCase(id)
        }
    }

    // Chamado quando o usuário clica no coração
    fun toggleFavorite() {
        val entity = currentPokemonEntity ?: return
        viewModelScope.launch {
            if (_isFavorite.value) {
                removeFavoriteUseCase(entity.id)
                _isFavorite.value = false
            } else {
                addFavoriteUseCase(entity)
                _isFavorite.value = true
            }
        }
    }

    // Método auxiliar para transformar os dados da API na Entidade do Room
    // Chame isso quando os detalhes do Pokemon chegarem da API
    fun setPokemonDataForFavoriting(
        id: Int, name: String, img: String, types: List<String>,
        hp: Int, atk: Int, def: Int, spd: Int, w: Int, h: Int
    ) {
        currentPokemonEntity = FavoritePokemonEntity(
            id, name, img, types, hp, atk, def, spd, w, h
        )
    }
}