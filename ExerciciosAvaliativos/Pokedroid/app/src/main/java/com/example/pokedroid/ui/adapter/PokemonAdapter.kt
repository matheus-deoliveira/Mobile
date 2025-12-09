package com.example.pokedroid.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pokedroid.databinding.ItemPokemonBinding
import com.example.pokedroid.domain.model.Pokemon

class PokemonAdapter(private val onClick: (Pokemon) -> Unit) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Pokemon>() {
        override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon) = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    inner class PokemonViewHolder(val binding: ItemPokemonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        return PokemonViewHolder(
            ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = differ.currentList[position]
        with(holder.binding) {
            tvName.text = pokemon.name
            tvNumber.text = "#%03d".format(pokemon.id)

            Glide.with(root).load(pokemon.imageUrl).into(ivSprite)

            // Configuração simples de cor (será melhorado no detalhe, aqui usamos padrão cinza ou baseado em ID se quiséssemos lógica local)
            cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"))

            root.setOnClickListener { onClick(pokemon) }
        }
    }

    override fun getItemCount() = differ.currentList.size
}