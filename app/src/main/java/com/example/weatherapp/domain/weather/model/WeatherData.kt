package com.example.weatherapp.domain.weather.model

import java.time.LocalDateTime

data class WeatherData(
    val time: LocalDateTime,
    val temperatureCelsius: Double,
    val desc: String,
    val iconCode: String,
    val pressure: Double,
    val windSpeed: Double,
    val humidity: Double,
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val country: String,
    var isLoading: Boolean = false
)