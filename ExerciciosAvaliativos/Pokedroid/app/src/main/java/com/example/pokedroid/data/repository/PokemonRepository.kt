package com.example.pokedroid.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokedroid.data.api.PokemonApiService
import com.example.pokedroid.data.api.PokemonPagingSource
import com.example.pokedroid.data.model.PokemonDetailResponse
import com.example.pokedroid.data.model.PokemonListResponse
import com.example.pokedroid.data.model.PokemonResult
import kotlinx.coroutines.flow.Flow

class PokemonRepository(private val apiService: PokemonApiService) {

    fun getPokemonStream(): Flow<PagingData<PokemonResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(apiService) }
        ).flow
    }

    suspend fun getPokemonDetails(name: String): PokemonDetailResponse {
        return apiService.getPokemonDetail(name.lowercase())
    }

    // Nova função para buscar a lista completa de Pokémon para a busca
    suspend fun getAllPokemons(): PokemonListResponse {
        // Um número alto para garantir que pegamos todos. A API tem mais de 1300.
        return apiService.getPokemonList(limit = 1500, offset = 0)
    }
}