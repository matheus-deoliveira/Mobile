package com.example.pokedroid.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.pokedroid.data.api.PokemonApiService
import com.example.pokedroid.data.api.PokemonPagingSource
import com.example.pokedroid.data.model.PokemonResult
import kotlinx.coroutines.flow.Flow

class PokemonRepository(private val apiService: PokemonApiService) {

    fun getPokemonStream(): Flow<PagingData<PokemonResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Quantidade de itens por página
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PokemonPagingSource(apiService) } // Nossa fonte de dados
        ).flow
    }

    // Aqui podemos adicionar mais tarde as funções para buscar detalhes de um pokemon
    // ou para interagir com o banco de dados de favoritos.
}