package com.example.pokedroid.data.model

data class PokemonResult(
    val name: String,
    val url: String
) {
    // Função auxiliar para extrair o ID numérico da URL
    // Ex: https://pokeapi.co/api/v2/pokemon/25/ -> Retorna 25
    fun getPokemonId(): Int {
        val splitUrl = url.split("/")
        // A url termina com barra, então o ID é o penúltimo item
        return splitUrl[splitUrl.size - 2].toInt()
    }

    // Função auxiliar para gerar a URL da imagem oficial
    fun getImageUrl(): String {
        val id = getPokemonId()
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    }
}