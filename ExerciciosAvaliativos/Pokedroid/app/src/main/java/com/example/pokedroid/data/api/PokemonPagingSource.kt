package com.example.pokedroid.data.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokedroid.data.model.PokemonResult
import java.io.IOException
import retrofit2.HttpException

private const val POKEMON_STARTING_PAGE_INDEX = 0

class PokemonPagingSource(
    private val pokemonApiService: PokemonApiService
) : PagingSource<Int, PokemonResult>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonResult> {
        val position = params.key ?: POKEMON_STARTING_PAGE_INDEX
        val offset = position * params.loadSize

        return try {
            val response = pokemonApiService.getPokemonList(limit = params.loadSize, offset = offset)
            val pokemons = response.results

            val nextKey = if (pokemons.isEmpty()) {
                null
            } else {
                // A API não informa o total, então continuamos carregando até vir uma lista vazia
                position + 1
            }

            LoadResult.Page(
                data = pokemons,
                prevKey = if (position == POKEMON_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            // Erro de I/O (ex: sem internet)
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            // Erro de HTTP (ex: 404 Not Found)
            LoadResult.Error(exception)
        }
    }

    // Este método define como recarregar os dados se a lista for invalidada
    override fun getRefreshKey(state: PagingState<Int, PokemonResult>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1) 
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}