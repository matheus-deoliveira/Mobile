package com.example.pokedroid.domain.model

data class PokemonDetail(
    val id: Int,
    val name: String,
    val height: Double, // Metros
    val weight: Double, // Kg
    val types: List<String>,
    val stats: List<Stat>,
    val imageUrl: String
)

data class Stat(
    val name: String,
    val value: Int,
    val max: Int = 255 // Valor máximo aproximado para barra de progresso
)