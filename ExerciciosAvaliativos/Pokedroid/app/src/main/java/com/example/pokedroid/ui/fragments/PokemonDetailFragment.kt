package com.example.pokedroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.pokedroid.data.api.RetrofitClient
import com.example.pokedroid.data.repository.PokemonRepository
import com.example.pokedroid.databinding.FragmentPokemonDetailBinding
import com.example.pokedroid.ui.viewmodel.PokemonDetailViewModel
import com.example.pokedroid.ui.viewmodel.PokemonDetailViewModelFactory

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PokemonDetailFragmentArgs by navArgs()
    private lateinit var viewModel: PokemonDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()

        // Inicia a busca pelos detalhes
        viewModel.getPokemonDetails(args.pokemonName)
    }

    private fun setupViewModel() {
        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = PokemonDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PokemonDetailViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.pokemonDetail.observe(viewLifecycleOwner) { pokemon ->
            binding.ivPokemonImage.isVisible = pokemon != null
            binding.tvPokemonName.isVisible = pokemon != null
            
            pokemon?.let {
                binding.tvPokemonName.text = it.name.replaceFirstChar { char -> char.uppercase() }
                Glide.with(this)
                    .load(it.sprites.other.officialArtwork.frontDefault)
                    .into(binding.ivPokemonImage)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}