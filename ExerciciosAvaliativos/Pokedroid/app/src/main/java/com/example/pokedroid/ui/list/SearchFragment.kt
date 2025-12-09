package com.example.pokedroid.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokedroid.databinding.FragmentSearchBinding
import com.example.pokedroid.service.ServiceLocator
import com.example.pokedroid.ui.adapter.PokemonAdapter
import com.example.pokedroid.ui.viewmodel.PokemonViewModel
import com.example.pokedroid.ui.viewmodel.PokemonViewModelFactory
import com.example.pokedroid.utils.Resource

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PokemonViewModel
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializa o ViewModel (Usando o Factory para passar o Repository)
        val repository = ServiceLocator.provideRepository(requireContext())
        val factory = PokemonViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[PokemonViewModel::class.java]

        setupRecyclerView()
        setupObservers()
        setupSearch()
    }

    private fun setupRecyclerView() {
        pokemonAdapter = PokemonAdapter { pokemon ->
            val action = SearchFragmentDirections.actionSearchToDetail(pokemon.name)
            findNavController().navigate(action)
        }

        binding.rvPokemon.apply {
            adapter = pokemonAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)

            // Lógica de Paginação (Scroll Listener)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && binding.etSearch.text.toString().isEmpty() // Não paginar se estiver buscando
                    ) {
                        viewModel.loadPokemonPaginated()
                    }
                }
            })
        }
    }

    private fun setupSearch() {
        // Ao clicar no botão de busca do teclado
        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchPokemon(v.text.toString())
                true
            } else false
        }

        // Listener opcional para limpar busca e voltar a lista original
        binding.etSearch.addTextChangedListener { text ->
            if(text.isNullOrEmpty()){
                viewModel.searchPokemon("")
            }
        }
    }

    private fun setupObservers() {
        viewModel.pokemonList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    pokemonAdapter.differ.submitList(resource.data)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}