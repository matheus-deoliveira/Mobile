package com.example.pokedroid.utils

import com.example.pokedroid.R

object PokemonTypeUtils {
    fun getColor(type: String): Int {
        return when (type.lowercase()) {
            "fire" -> R.color.type_fire
            "water" -> R.color.type_water
            "grass" -> R.color.type_grass
            "electric" -> R.color.type_electric
            "psychic" -> R.color.type_psychic
            "ice" -> R.color.type_ice
            "dragon" -> R.color.type_dragon
            "fairy" -> R.color.type_fairy
            "fighting" -> R.color.type_fighting
            "poison" -> R.color.type_poison
            "ground" -> R.color.type_ground
            "flying" -> R.color.type_flying
            "bug" -> R.color.type_bug
            "rock" -> R.color.type_rock
            "ghost" -> R.color.type_ghost
            "steel" -> R.color.type_steel
            else -> R.color.type_normal
        }
    }
}