package com.example.pokedroid

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pokedroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Conecta o BottomNav com o NavController
        binding.bottomNavigation.setupWithNavController(navController)

        // Controle de Visibilidade da BottomBar (Ocultar na Splash ou Detalhes se quiser)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchFragment, R.id.favoriteListFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    // Ocultar em detalhes ou splash se preferir, ou manter visível
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }
}