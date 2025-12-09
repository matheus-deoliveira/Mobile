package com.example.pokedroid.data.remote.dto

import com.google.gson.annotations.SerializedName

// List Response
data class PokemonListResponse(
    val results: List<PokemonResultDto>
)

data class PokemonResultDto(
    val name: String,
    val url: String
)

// Detail Response
data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int, // Decimetros
    val weight: Int, // Hectogramas
    val types: List<TypeSlotDto>,
    val stats: List<StatDto>,
    val sprites: SpritesDto
)

data class TypeSlotDto(val type: TypeDto)
data class TypeDto(val name: String)
data class StatDto(val base_stat: Int, val stat: StatNameDto)
data class StatNameDto(val name: String)
data class SpritesDto(
    @SerializedName("other") val other: OtherSprites
)
data class OtherSprites(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtwork
)
data class OfficialArtwork(
    @SerializedName("front_default") val frontDefault: String
)