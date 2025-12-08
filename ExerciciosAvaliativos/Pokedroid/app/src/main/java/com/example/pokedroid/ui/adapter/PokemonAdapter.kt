package com.example.pokedroid.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedroid.data.model.PokemonResult
import com.example.pokedroid.databinding.ItemPokemonBinding

class PokemonAdapter(
    private val onClick: (PokemonResult) -> Unit
) : PagingDataAdapter<PokemonResult, PokemonAdapter.PokemonViewHolder>(PokemonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = getItem(position)
        // O PagingDataAdapter pode retornar null para placeholders, então verificamos
        pokemon?.let { holder.bind(it, onClick) }
    }

    class PokemonViewHolder(private val binding: ItemPokemonBinding) : RecyclerView.ViewHolder(binding.root) {
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