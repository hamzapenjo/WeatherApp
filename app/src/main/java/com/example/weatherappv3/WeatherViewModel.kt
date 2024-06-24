package com.example.weatherappv3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class WeatherViewModel : ViewModel() {
    private val weatherService: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    var city by mutableStateOf("Kakanj")
    var temperature by mutableStateOf("Fetching...")
    var humidity by mutableStateOf("")
    var pressure by mutableStateOf("")
    var windSpeed by mutableStateOf("")
    var weatherIconUrl by mutableStateOf("")

    fun fetchWeather(apiKey: String) {
        viewModelScope.launch {
            try {
                val response = weatherService.getWeather(city, apiKey)
                val tempInCelsius = (response.main.temp - 273.15).roundToInt()
                temperature = "$tempInCelsius°C"
                humidity = "${response.main.humidity}%"
                pressure = "${response.main.pressure} hPa"
                windSpeed = "${response.wind.speed} m/s"
                weatherIconUrl = "https://openweathermap.org/img/w/${response.weather[0].icon}.png"
            } catch (e: Exception) {
                temperature = "Failed to fetch data"
                humidity = "Failed to fetch data"
                pressure = "Failed to fetch data"
                windSpeed = "Failed to fetch data"
            }
        }
    }

    fun updateCity(newCity: String) {
        city = newCity
    }
}
