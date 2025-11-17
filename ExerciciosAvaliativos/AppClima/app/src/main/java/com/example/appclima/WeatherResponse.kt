package com.example.appclima

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main") val main : Main,
    @SerializedName("wind") val wind : Wind,
    @SerializedName("weather") val weather : List<WeatherDescription>,
    @SerializedName("name") val cityName : String
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    @SerializedName("humidity") val humidity: Int
)

data class Wind(
    @SerializedName("speed") val speed: Double
)

data class WeatherDescription(
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)