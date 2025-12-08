package com.example.pokedroid.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "favoritePokemon")
data class FavoritePokemon(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String
    // Dá para adicionar stats também
)

@Dao
interface FavoritePokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(pokemon: FavoritePokemon)

    @Delete
    suspend fun removeFavorite(pokemon: FavoritePokemon)

    @Query("SELECT * FROM favoritePokemon")
    fun getAllFavoritePokemon(): LiveData<List<FavoritePokemon>>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritePokemon WHERE id = :id)")
    suspend fun isFavoritePokemon(id: Int): Boolean
}