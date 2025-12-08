package com.example.pokedroid.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.pokedroid.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Instala a nova API de Splash Screen
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Não precisamos de setContentView, pois o tema da splash já cuida da aparência.

        // 2. Lança uma corrotina para esperar 3 segundos antes de navegar
        lifecycleScope.launch {
            delay(3000) // Espera por 3000 milissegundos (3 segundos)

            // 3. Navega para a MainActivity
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // Impede que o usuário volte para a splash screen
        }
    }
}