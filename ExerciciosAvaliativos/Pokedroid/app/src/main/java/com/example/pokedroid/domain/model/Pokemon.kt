package com.example.pokedroid.domain.model

import java.io.Serializable

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val color: Int // Mapeado localmente para recurso de cor
) : Serializable