package com.example.pokedroid.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import com.example.pokedroid.domain.model.Pokemon
import com.example.pokedroid.domain.model.PokemonDetail
import com.example.pokedroid.domain.repository.PokemonRepository
import com.example.pokedroid.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PokemonViewModel(private val repository: PokemonRepository) : ViewModel() {

    // --- Lógica de Lista e Paginação ---
    private val _pokemonList = MutableLiveData<Resource<List<Pokemon>>>()
    val pokemonList: LiveData<Resource<List<Pokemon>>> = _pokemonList

    private var curPage = 0
    private var isLastPage = false
    private var isLoading = false
    private val pageSize = 10
    private val cachedList = mutableListOf<Pokemon>()

    init {
        loadPokemonPaginated()
    }

    fun loadPokemonPaginated() {
        if (isLoading || isLastPage) return

        isLoading = true
        _pokemonList.postValue(Resource.Loading())

        viewModelScope.launch {
            val response = repository.getPokemonList(pageSize, curPage * pageSize)
            isLoading = false
            when (response) {
                is Resource.Success -> {
                    response.data?.let { newItems ->
                        if (newItems.isEmpty()) {
                            isLastPage = true
                        } else {
                            curPage++
                            cachedList.addAll(newItems)
                            _pokemonList.postValue(Resource.Success(cachedList.toList()))
                        }
                    }
                }
                is Resource.Error -> {
                    _pokemonList.postValue(Resource.Error(response.message ?: "Erro desconhecido"))
                }
                else -> Unit
            }
        }
    }

    fun searchPokemon(query: String) {
        if (query.isEmpty()) {
            _pokemonList.value = Resource.Success(cachedList)
            return
        }

        viewModelScope.launch {
            _pokemonList.postValue(Resource.Loading())
            val response = repository.getPokemonDetail(query)
            when (response) {
                is Resource.Success -> {
                    response.data?.let { detail ->
                        val p = Pokemon(detail.id, detail.name, detail.imageUrl, detail.types, 0)
                        _pokemonList.postValue(Resource.Success(listOf(p)))
                    }
                }
                is Resource.Error -> {
                    _pokemonList.postValue(Resource.Error("Pokémon não encontrado."))
                }
                else -> Unit
            }
        }
    }

    // --- Lógica de Detalhes ---
    private val _pokemonDetail = MutableLiveData<Resource<PokemonDetail>>()
    val pokemonDetail: LiveData<Resource<PokemonDetail>> = _pokemonDetail

    fun getPokemonDetail(name: String) {
        _pokemonDetail.postValue(Resource.Loading())
        viewModelScope.launch {
            val response = repository.getPokemonDetail(name)
            _pokemonDetail.postValue(response)
        }
    }

    // --- Lógica de Favoritos ---
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private var currentPokemonEntity: FavoritePokemonEntity? = null

    fun checkFavoriteStatus(id: Int) {
        viewModelScope.launch {
            // O Repositório deve retornar Boolean se o Pokémon existe no banco
            val exists = repository.isFavorite(id)
            _isFavorite.value = exists
        }
    }

    fun toggleFavorite() {
        val entity = currentPokemonEntity ?: return
        viewModelScope.launch {
            if (_isFavorite.value) {
                // Se já é favorito, remove
                repository.removeFavorite(entity)
                _isFavorite.value = false
            } else {
                // Se não é, adiciona
                repository.insertFavorite(entity)
                _isFavorite.value = true
            }
        }
    }

    fun setPokemonDataForFavoriting(detail: PokemonDetail) {
        currentPokemonEntity = FavoritePokemonEntity(
            id = detail.id,
            name = detail.name,
            imageUrl = detail.imageUrl,
            types = detail.types,
            hp = detail.stats.find { it.name == "hp" }?.value ?: 0,
            attack = detail.stats.find { it.name == "attack" }?.value ?: 0,
            defense = detail.stats.find { it.name == "defense" }?.value ?: 0,
            speed = detail.stats.find { it.name == "speed" }?.value ?: 0,
            weight = detail.weight.toInt(),
            height = detail.height.toInt()
        )
    }
}

// Factory para injetar repo
class PokemonViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PokemonViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}