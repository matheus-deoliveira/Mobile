package com.example.appclima.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appclima.RetrofitClient
import com.example.appclima.WeatherResponse
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {

    private val API_KEY = "5fa2596b60f6332dde093e9d25eedd73"

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    // LiveData para erro (contém a mensagem de erro)
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchWeatherData(cityName: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val apiService = RetrofitClient.instance
                val response = apiService.getCurrentWeather(cityName, API_KEY)

                // Sucesso: postamos o valor para a Activity observar
                _weatherData.value = response

            } catch (e: Exception) {
                _errorMessage.value = "Erro ao buscar dados: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}