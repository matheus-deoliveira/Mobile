package com.example.pokedroid.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoritePokemon::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritePokemonDao(): FavoritePokemonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // O erro estava ocorrendo nesta lógica abaixo:
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokedroid_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}