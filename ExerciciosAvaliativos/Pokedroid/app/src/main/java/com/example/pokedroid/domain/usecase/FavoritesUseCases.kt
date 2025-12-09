package com.example.pokedroid.domain.usecase

import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import com.example.pokedroid.data.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow

class AddFavoriteUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(pokemon: FavoritePokemonEntity) = repository.insertFavorite(pokemon)
}

class RemoveFavoriteUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(id: Int) = repository.removeFavorite(id)
}

class GetFavoriteStatusUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(id: Int): Boolean = repository.isFavorite(id)
}

class GetAllFavoritesUseCase(private val repository: PokemonRepository) {
    operator fun invoke(): Flow<List<FavoritePokemonEntity>> = repository.allFavorites
}