package com.example.pokedroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedroid.data.api.RetrofitClient
import com.example.pokedroid.data.repository.PokemonRepository
import com.example.pokedroid.databinding.FragmentSearchResultBinding
import com.example.pokedroid.ui.adapter.PokemonAdapter
import com.example.pokedroid.ui.viewmodel.SearchResultViewModel
import com.example.pokedroid.ui.viewmodel.SearchResultViewModelFactory
import kotlinx.coroutines.launch

class SearchResultFragment : Fragment() {

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SearchResultViewModel
    private lateinit var pokemonAdapter: PokemonAdapter
    private val args: SearchResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()

        viewModel.searchPokemon(args.query)
    }

    private fun setupViewModel() {
        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = SearchResultViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SearchResultViewModel::class.java]
    }

    private fun setupRecyclerView() {
        pokemonAdapter = PokemonAdapter {
            val action = SearchResultFragmentDirections.actionSearchResultFragmentToPokemonDetailFragment(it.name)
            findNavController().navigate(action)
        }
        binding.rvSearchResult.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResult.adapter = pokemonAdapter
    }

    private fun setupObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            // Corrigido: Chama a função suspend dentro de uma corrotina
            viewLifecycleOwner.lifecycleScope.launch {
                pokemonAdapter.submitListAsList(results)
            }
            if (results.isEmpty()) {
                Toast.makeText(requireContext(), "Nenhum Pokémon encontrado com '${args.query}'", Toast.LENGTH_LONG).show()
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