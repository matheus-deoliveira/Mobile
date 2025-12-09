package com.example.pokedroid.data.repository

import com.example.pokedroid.data.local.dao.FavoritePokemonDao
import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import com.example.pokedroid.data.remote.api.PokeApi
import com.example.pokedroid.domain.model.Pokemon
import com.example.pokedroid.domain.model.PokemonDetail
import com.example.pokedroid.domain.model.Stat
import com.example.pokedroid.domain.repository.PokemonRepository
import com.example.pokedroid.utils.PokemonTypeUtils
import com.example.pokedroid.utils.Resource
import java.io.IOException

class PokemonRepositoryImpl(private val api: PokeApi, private val dao: FavoritePokemonDao) : PokemonRepository {

    override suspend fun getPokemonList(limit: Int, offset: Int): Resource<List<Pokemon>> {
        return try {
            val response = api.getPokemonList(limit, offset)
            val pokemonList = response.results.map { dto ->
                // Extrair ID da URL para pegar a imagem sem fazer request extra
                val id = dto.url.split("/").dropLast(1).last().toInt()
                val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

                Pokemon(
                    id = id,
                    name = dto.name.replaceFirstChar { it.uppercase() },
                    imageUrl = imageUrl,
                    types = emptyList(), // Lista simples não traz tipos, detalhe traz
                    color = PokemonTypeUtils.getColor("normal") // Placeholder
                )
            }
            Resource.Success(pokemonList)
        } catch (e: Exception) {
            Resource.Error("Erro ao carregar lista: ${e.message}")
        }
    }

    override suspend fun getPokemonDetail(name: String): Resource<PokemonDetail> {
        return try {
            val dto = api.getPokemonDetailPath(name.lowercase())
            val detail = PokemonDetail(
                id = dto.id,
                name = dto.name.replaceFirstChar { it.uppercase() },
                height = dto.height / 10.0, // decimetro para metro
                weight = dto.weight / 10.0, // hectograma para kg
                types = dto.types.map { it.type.name },
                stats = dto.stats.map { Stat(it.stat.name, it.base_stat) },
                imageUrl = dto.sprites.other.officialArtwork.frontDefault
            )
            Resource.Success(detail)
        } catch (e: IOException) {
            Resource.Error("Erro de conexão. Verifique sua internet.")
        } catch (e: Exception) {
            Resource.Error("Não foi possível encontrar este Pokémon.")
        }
    }

    override suspend fun isFavorite(id: Int): Boolean {
        return dao.isFavorite(id)
    }

    override suspend fun insertFavorite(pokemon: FavoritePokemonEntity) {
        dao.insertFavorite(pokemon)
    }

    override suspend fun removeFavorite(pokemon: FavoritePokemonEntity) {
        dao.deleteFavorite(pokemon)
    }
}