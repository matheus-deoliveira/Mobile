package com.example.appclima

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.appclima.databinding.ActivityReportScreenBinding
import kotlinx.coroutines.launch
import java.util.Locale

class ReportScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportScreenBinding

    private lateinit var sharedPreferences: SharedPreferences

    private val API_KEY = "5fa2596b60f6332dde093e9d25eedd73"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityReportScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.includeActionBar.arrowBack.setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_report_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("AppClimaPrefs", MODE_PRIVATE)

        val cidadeSalva = sharedPreferences.getString("nome_cidade", "Vitória") ?: "Vitória"

        Log.d("ReportScreenActivity", "Cidade lida do SharedPreferences: $cidadeSalva")

        if (cidadeSalva.isNotEmpty()) {
            fetchWeatherData(cidadeSalva)
        } else {
            // Caso de erro: se a cidade estiver vazia
            binding.nomeCidade.text = "Nenhuma cidade selecionada"
        }
    }

    private fun fetchWeatherData(cityName: String) {
        // Usei o 'lifecycleScope' para rodar a chamada em background (Coroutines)
        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.instance
                val weatherResponse = apiService.getCurrentWeather(cityName, API_KEY)

                updateUI(weatherResponse)

            } catch (e: Exception) {
                // 5. Se der erro
                Log.e("ReportScreenActivity", "Erro ao buscar dados: ${e.message}")
                // Você pode mostrar uma mensagem de erro para o usuário aqui
                binding.nomeCidade.text = "Erro ao carregar"
                binding.stateText.text = "Verifique sua conexão ou API Key"
            }
        }
    }

    private fun updateUI(data: WeatherResponse) {
        // Pega os dados da resposta e formata
        val tempAtual = data.main.temp.toInt() // Arredonda para inteiro
        val tempMin = data.main.tempMin.toInt()
        val tempMax = data.main.tempMax.toInt()
        val sensacao = data.main.feelsLike.toInt()
        val umidade = data.main.humidity
        val vento = data.wind.speed.toInt()
        val descricao = data.weather.firstOrNull()?.description?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        } ?: "N/A"

        val weatherInfo = data.weather.firstOrNull()

        val iconCode = weatherInfo?.icon
        if (iconCode != null) {
            // Monta a URL completa da imagem (usando @2x.png para qualidade melhor)
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"

            // Usa a Coil para carregar a imagem da URL no seu ImageView
            binding.stateImage.load(iconUrl) {
                crossfade(true) // Adiciona um efeito suave de transição
            }
        }

        // Atualiza os TextViews do seu layout
        binding.nomeCidade.text = data.cityName
        binding.stateText.text = "$tempAtual°C, $descricao"
        binding.maxTemperature.text = "Temperatura máx: $tempMax°C"
        binding.minTemperature.text = "Temperatura mín: $tempMin°C"
        binding.sensacaoTermica.text = "Sensação térmica: $sensacao°C"
        binding.windVelocity.text = "Velocidade do vento: $vento km/h"
        binding.airHumidity.text = "Humidade do ar: $umidade%"

        // (Opcional) Você pode usar o "data.weather.firstOrNull()?.icon"
        // para carregar a imagem do tempo, mas isso requer outra biblioteca (Glide ou Coil)
    }
}