package com.example.pokedroid.domain.repository

import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import com.example.pokedroid.domain.model.Pokemon
import com.example.pokedroid.domain.model.PokemonDetail
import com.example.pokedroid.utils.Resource

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<List<Pokemon>>
    suspend fun getPokemonDetail(name: String): Resource<PokemonDetail>

    suspend fun isFavorite(id: Int): Boolean
    suspend fun insertFavorite(pokemon: FavoritePokemonEntity)
    suspend fun removeFavorite(pokemon: FavoritePokemonEntity)
}