package com.example.pokedroid.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pokedroid.data.local.AppDatabase
import com.example.pokedroid.data.repository.PokemonRepository
import com.example.pokedroid.databinding.FragmentFavoriteListBinding
import com.example.pokedroid.domain.usecase.GetAllFavoritesUseCase
import com.example.pokedroid.domain.usecase.RemoveFavoriteUseCase
import kotlinx.coroutines.launch

class FavoriteListFragment : Fragment() {

    private var _binding: FragmentFavoriteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FavoriteListViewModel
    private lateinit var adapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDependencies()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupDependencies() {
        // Inicialização Manual (Simulando Injeção de Dependência)
        val dao = AppDatabase.getDatabase(requireContext()).favoriteDao()
        val daoRepository = PokemonRepository(dao) // Verifique se o construtor do Repo bate com isso
        val getAllUseCase = GetAllFavoritesUseCase(daoRepository)
        val removeUseCase = RemoveFavoriteUseCase(daoRepository)

        val factory = FavoriteListViewModelFactory(getAllUseCase, removeUseCase)
        viewModel = ViewModelProvider(this, factory)[FavoriteListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        // CORREÇÃO AQUI: O lambda agora recebe uma String (pokemonName)
        adapter = FavoritesAdapter { pokemonName ->
            val action = FavoriteListFragmentDirections.actionFavoritesToDetails(pokemonName)
            findNavController().navigate(action)
        }
        binding.rvFavorites.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favorites.collect { list ->
                adapter.submitList(list)
                binding.tvEmptyFavorites.isVisible = list.isEmpty()
                binding.rvFavorites.isVisible = list.isNotEmpty()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}