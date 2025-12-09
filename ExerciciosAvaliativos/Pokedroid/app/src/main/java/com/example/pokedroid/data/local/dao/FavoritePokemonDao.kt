package com.example.pokedroid.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePokemonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pokemon: FavoritePokemonEntity)

    @Delete
    suspend fun deleteFavorite(pokemon: FavoritePokemonEntity)

    // Deletar pelo ID caso não tenha o objeto completo na hora
    @Query("DELETE FROM favorite_pokemons WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int)

    @Query("SELECT * FROM favorite_pokemons")
    fun getAllFavorites(): Flow<List<FavoritePokemonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pokemons WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("SELECT * FROM favorite_pokemons WHERE id = :id LIMIT 1")
    suspend fun getFavoriteById(id: Int): FavoritePokemonEntity?
}