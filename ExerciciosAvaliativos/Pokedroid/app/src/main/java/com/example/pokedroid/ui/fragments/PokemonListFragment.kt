package com.example.pokedroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokedroid.data.api.RetrofitClient
import com.example.pokedroid.data.repository.PokemonRepository
import com.example.pokedroid.databinding.FragmentPokemonListBinding
import com.example.pokedroid.ui.adapter.PokemonAdapter
import com.example.pokedroid.ui.adapter.PokemonLoadStateAdapter
import com.example.pokedroid.ui.viewmodel.PokemonViewModel
import com.example.pokedroid.ui.viewmodel.PokemonViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PokemonListFragment : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PokemonAdapter
    private lateinit var viewModel: PokemonViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupViewModel() {
        val repository = PokemonRepository(RetrofitClient.instance)
        val factory = PokemonViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PokemonViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = PokemonAdapter { pokemon ->
            Toast.makeText(requireContext(), "Você clicou no ${pokemon.name.uppercase()}", Toast.LENGTH_SHORT).show()
        }

        binding.rvPokemonList.layoutManager = LinearLayoutManager(requireContext())
        
        // Conecta o adapter principal com o adapter de estado de carregamento
        binding.rvPokemonList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = PokemonLoadStateAdapter { adapter.retry() },
            footer = PokemonLoadStateAdapter { adapter.retry() }
        )

        // Adiciona um listener para mostrar/esconder uma view de loading inicial
        adapter.addLoadStateListener { loadState ->
            // Mostra a lista apenas quando não está carregando e não há erro
            binding.rvPokemonList.isVisible = loadState.source.refresh is LoadState.NotLoading
            // Futuramente, podemos adicionar um ProgressBar para o estado de loading inicial
            // e uma view de erro para o estado de erro inicial.
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pokemonPagingData.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}