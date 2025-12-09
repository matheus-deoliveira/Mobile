package com.example.pokedroid.data.repository

import com.example.pokedroid.data.local.dao.FavoritePokemonDao
import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import kotlinx.coroutines.flow.Flow

class PokemonRepository(
    // Adicione o DAO aqui no construtor. Se usar injeção manual, passe na criação.
    private val favoriteDao: FavoritePokemonDao
    // private val api: PokemonApi (já existente)
) {
    // ... seus métodos da API existentes

    // Métodos Locais
    val allFavorites: Flow<List<FavoritePokemonEntity>> = favoriteDao.getAllFavorites()

    suspend fun insertFavorite(pokemon: FavoritePokemonEntity) {
        favoriteDao.insertFavorite(pokemon)
    }

    suspend fun removeFavorite(id: Int) {
        favoriteDao.deleteFavoriteById(id)
    }

    suspend fun isFavorite(id: Int): Boolean {
        return favoriteDao.isFavorite(id)
    }
}