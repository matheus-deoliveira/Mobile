package com.example.pokedroid.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pokedroid.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSearch.setOnClickListener {
            val pokemonNameQuery = binding.etPokemonName.text.toString().trim()

            if (pokemonNameQuery.isNotEmpty()) {
                // Se o nome foi digitado, navega para a nova tela de resultados da busca
                val action = SearchFragmentDirections.actionSearchFragmentToSearchResultFragment(pokemonNameQuery)
                findNavController().navigate(action)
            } else {
                // Se estiver vazio, navega para a lista completa paginada
                val action = SearchFragmentDirections.actionSearchFragmentToPokemonListFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}