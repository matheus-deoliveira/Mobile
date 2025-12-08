package com.example.pokedroid.data.api

import com.example.pokedroid.data.model.PokemonDetailResponse
import com.example.pokedroid.data.model.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonApiService {

    // Esta função resolve o erro 'getPokemonList'
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    // Esta função resolve o erro 'getPokemonDetail'
    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ): PokemonDetailResponse
}