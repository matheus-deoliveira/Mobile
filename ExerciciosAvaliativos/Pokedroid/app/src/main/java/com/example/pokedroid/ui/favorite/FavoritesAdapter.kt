package com.example.pokedroid.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedroid.data.local.entity.FavoritePokemonEntity
import com.example.pokedroid.databinding.ItemFavoritePokemonBinding

class FavoritesAdapter(private val onItemClick: (String) -> Unit) :
    ListAdapter<FavoritePokemonEntity, FavoritesAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    inner class FavoriteViewHolder(private val binding: ItemFavoritePokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemon: FavoritePokemonEntity) {
            binding.tvFavoriteName.text = pokemon.name.replaceFirstChar { it.uppercase() }
            binding.chipFavoriteType.text = pokemon.types.firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "Normal"

            Glide.with(binding.root)
                .load(pokemon.imageUrl)
                .into(binding.ivFavoriteSprite)

            binding.root.setOnClickListener { onItemClick(pokemon.name) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoritePokemonBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoritePokemonEntity>() {
            override fun areItemsTheSame(oldItem: FavoritePokemonEntity, newItem: FavoritePokemonEntity) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: FavoritePokemonEntity, newItem: FavoritePokemonEntity) =
                oldItem == newItem
        }
    }
}