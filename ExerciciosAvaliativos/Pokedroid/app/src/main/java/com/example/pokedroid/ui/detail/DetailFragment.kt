package com.example.pokedroid.ui.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.pokedroid.R
import com.example.pokedroid.databinding.FragmentDetailBinding
import com.example.pokedroid.domain.model.PokemonDetail
import com.example.pokedroid.service.ServiceLocator
import com.example.pokedroid.ui.viewmodel.PokemonViewModel
import com.example.pokedroid.ui.viewmodel.PokemonViewModelFactory
import com.example.pokedroid.utils.PokemonTypeUtils
import com.example.pokedroid.utils.Resource
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    // Argumentos recebidos via Navigation (Safe Args)
    private val args: DetailFragmentArgs by navArgs()

    private lateinit var viewModel: PokemonViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Inicializa o ViewModel
        val repository = ServiceLocator.provideRepository(requireContext())
        val factory = PokemonViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[PokemonViewModel::class.java]

        // 2. Observa o estado de favorito
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavorite.collect { isFav ->
                val iconRes = if (isFav) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
                binding.fabFavorite.setImageResource(iconRes)
            }
        }

        // 3. Configura o clique do botão de favorito
        binding.fabFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        // 4. Busca os detalhes
        viewModel.getPokemonDetail(args.pokemonName)

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.pokemonDetail.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { detail ->
                        populateUI(detail)
                        viewModel.setPokemonDataForFavoriting(detail)
                        viewModel.checkFavoriteStatus(detail.id)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Opcional: mostrar loading
                }
            }
        }
    }

    private fun populateUI(detail: PokemonDetail) {
        binding.tvDetailName.text = detail.name.replaceFirstChar { it.uppercase() }

        Glide.with(this)
            .load(detail.imageUrl)
            .into(binding.ivDetailImage)

        // --- Configuração dos Chips de Tipo ---
        binding.chipGroupTypes.removeAllViews()
        detail.types.forEach { typeName ->
            val chip = Chip(requireContext()).apply {
                text = typeName.replaceFirstChar { it.uppercase() }
                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(context, PokemonTypeUtils.getColor(typeName))
                )
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            binding.chipGroupTypes.addView(chip)
        }

        // --- Configuração dos Stats (Barras de progresso Coloridas) ---
        binding.statsContainer.removeAllViews()
        detail.stats.forEach { stat ->
            val statView = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16 }
            }

            val tvLabel = TextView(requireContext()).apply {
                // Formata o texto (ex: "HP: 45")
                text = "${stat.name.uppercase()}: ${stat.value}"
                textSize = 12f
            }

            val progress = com.google.android.material.progressindicator.LinearProgressIndicator(requireContext()).apply {
                max = 200 // Valor máximo estimado
                this.progress = stat.value

                // Track Color (fundo da barra) - Deixei um cinza bem claro para destacar a cor
                trackColor = ContextCompat.getColor(context, android.R.color.darker_gray)
                alpha = 1.0f // Opacidade total

                // --- AQUI ESTÁ A MUDANÇA DA COR ---
                val colorRes = getStatColor(stat.name)
                setIndicatorColor(ContextCompat.getColor(context, colorRes))
            }

            statView.addView(tvLabel)
            statView.addView(progress)
            binding.statsContainer.addView(statView)
        }
    }

    // Função auxiliar para pegar a cor baseada no nome do status
    private fun getStatColor(statName: String): Int {
        return when (statName.lowercase()) {
            "hp" -> R.color.stat_hp
            "attack" -> R.color.stat_attack
            "defense" -> R.color.stat_defense
            "special-attack" -> R.color.stat_sp_attack
            "special-defense" -> R.color.stat_sp_defense
            "speed" -> R.color.stat_speed
            else -> R.color.stat_default
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}