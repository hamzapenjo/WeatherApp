package com.example.weatherappv3

import Weather
import WeatherData
import Wind
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    suspend fun getWeather(@Query("q") city: String, @Query("appid") apiKey: String): WeatherResponse
}

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val humidity: Int,
    val pressure: Int
)

data class Weather(
    val icon: String,
    val description: String
)

data class Wind(
    val speed: Double
)

