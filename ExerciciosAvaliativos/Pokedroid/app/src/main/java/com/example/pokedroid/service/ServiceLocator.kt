package com.example.pokedroid.service

import android.content.Context
import com.example.pokedroid.data.local.AppDatabase // Importe seu AppDatabase
import com.example.pokedroid.data.remote.api.PokeApi
import com.example.pokedroid.data.repository.PokemonRepositoryImpl
import com.example.pokedroid.domain.repository.PokemonRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    private val api: PokeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)
    }

    // Mudamos de 'val' para 'fun' para injetar o Contexto
    fun provideRepository(context: Context): PokemonRepository {
        // 1. Pega a instância do banco usando seu método Singleton
        val database = AppDatabase.getDatabase(context)

        // 2. Pega o DAO específico
        val dao = database.favoriteDao()

        // 3. Retorna o repositório com API e DAO
        return PokemonRepositoryImpl(api, dao)
    }
}