package com.example.pokedroid.data.remote.api

import com.example.pokedroid.data.remote.dto.PokemonDetailDto
import com.example.pokedroid.data.remote.dto.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Query("name") name: String
    ): PokemonDetailDto // Endpoint genérico, aceita ID ou Nome na URL, mas via Retrofit @Path é melhor

    // Correção: Usando Path para detalhe
    @GET("pokemon/{name}")
    suspend fun getPokemonDetailPath(
        @Path("name") name: String
    ): PokemonDetailDto
}