package com.example.pokedroid.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_pokemons")
data class FavoritePokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>, // Precisa de Converter
    val hp: Int,
    val attack: Int,
    val defense: Int,
    val speed: Int,
    val weight: Int,
    val height: Int
)