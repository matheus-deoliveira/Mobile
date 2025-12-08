package com.example.pokedroid.data.model

import com.google.gson.annotations.SerializedName

data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<PokemonTypeSlot>,
    val sprites: Sprites
)

data class PokemonTypeSlot(
    val slot: Int,
    val type: PokemonType
)

data class PokemonType(
    val name: String,
    val url: String
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String,
    @SerializedName("other")
    val other: OtherSprites
)

data class OtherSprites(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork
)

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String
)