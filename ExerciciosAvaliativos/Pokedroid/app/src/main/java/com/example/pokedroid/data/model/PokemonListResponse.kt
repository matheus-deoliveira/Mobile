package com.example.pokedroid.data.model

import com.google.gson.annotations.SerializedName

data class PokemonListResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("next") val next: String?,
    @SerializedName("previous") val previous: String?,

    // Esta é a linha que falta e que causa o erro no ViewModel
    @SerializedName("results") val results: List<PokemonResult>
)