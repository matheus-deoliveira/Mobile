package com.example.pokedroid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedroid.data.model.PokemonResult
import com.example.pokedroid.databinding.ItemPokemonBinding

class SearchResultAdapter(
    private val onClick: (PokemonResult) -> Unit
) : ListAdapter<PokemonResult, SearchResultAdapter.SearchResultViewHolder>(PokemonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val pokemon = getItem(position)
        holder.bind(pokemon, onClick)
    }

    class SearchResultViewHolder(private val binding: ItemPokemonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemon: PokemonResult, onClick: (PokemonResult) -> Unit) {
            binding.tvPokemonName.text = pokemon.name.replaceFirstChar { it.uppercase() }

            Glide.with(itemView.context)
                .load(pokemon.getImageUrl())
                .into(binding.ivPokemonImage)

            itemView.setOnClickListener { onClick(pokemon) }
        }
    }

    class PokemonDiffCallback : DiffUtil.ItemCallback<PokemonResult>() {
        override fun areItemsTheSame(oldItem: PokemonResult, newItem: PokemonResult): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PokemonResult, newItem: PokemonResult): Boolean {
            return oldItem == newItem
        }
    }
}