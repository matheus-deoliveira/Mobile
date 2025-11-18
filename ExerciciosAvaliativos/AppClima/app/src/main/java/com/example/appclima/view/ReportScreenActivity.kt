package com.example.appclima.view

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.appclima.R
import com.example.appclima.WeatherResponse
import com.example.appclima.databinding.ActivityReportScreenBinding
import com.example.appclima.viewmodel.ReportViewModel
import java.util.Locale

class ReportScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    // Inicialização do ViewModel
    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityReportScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLayout()
        setupObservers() // Configura os observadores do MVVM

        // Lógica de carregar a cidade
        sharedPreferences = getSharedPreferences("AppClimaPrefs", MODE_PRIVATE)
        val cidadeSalva = sharedPreferences.getString("nome_cidade", "Vitória") ?: "Vitória"

        Log.d("ReportScreenActivity", "Cidade lida do SharedPreferences: $cidadeSalva")

        if (cidadeSalva.isNotEmpty()) {
            // A Activity pede para o ViewModel buscar os dados
            viewModel.fetchWeatherData(cidadeSalva)
        } else {
            binding.nomeCidade.text = "Nenhuma cidade selecionada"
        }
    }

    private fun setupLayout() {
        binding.includeActionBar.arrowBack.setOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_report_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupObservers() {
        // 1. Observa os DADOS DE SUCESSO
        viewModel.weatherData.observe(this) { weatherResponse ->
            updateUI(weatherResponse)
        }

        // 2. Observa os ERROS
        viewModel.errorMessage.observe(this) { msg ->
            Log.e("ReportScreenActivity", msg)
            binding.nomeCidade.text = "Erro ao carregar"
            binding.stateText.text = "Verifique conexão/cidade"
            Toast.makeText(this, "Falha: $msg", Toast.LENGTH_LONG).show()
        }

        // 3. Observa o LOADING (Opcional, se quiser adicionar um ProgressBar no futuro)
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.nomeCidade.text = "Carregando..."
            }
        }
    }

    private fun updateUI(data: WeatherResponse) {
        // A lógica de UI permanece aqui pois envolve manipular Views diretamente e Coil
        val tempAtual = data.main.temp.toInt()
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
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
            binding.stateImage.load(iconUrl) {
                crossfade(true)
            }
        }

        binding.nomeCidade.text = data.cityName
        binding.stateText.text = "$tempAtual°C, $descricao"
        binding.maxTemperature.text = "Temperatura máx: $tempMax°C"
        binding.minTemperature.text = "Temperatura mín: $tempMin°C"
        binding.sensacaoTermica.text = "Sensação térmica: $sensacao°C"
        binding.windVelocity.text = "Velocidade do vento: $vento km/h"
        binding.airHumidity.text = "Humidade do ar: $umidade%"
    }
}