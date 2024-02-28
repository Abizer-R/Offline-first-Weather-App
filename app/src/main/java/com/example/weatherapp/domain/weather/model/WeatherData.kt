package com.example.weatherapp.domain.weather.model

import java.time.LocalDateTime

data class WeatherData(
    val isCurrentLocation: Boolean = false,
    val time: LocalDateTime,
    val temperatureCelsius: Double,
    val desc: String,
    val iconCode: String,
    val pressure: Double,
    val windSpeed: Double,
    val humidity: Double,
    var isLoading: Boolean = false,
    val locationDetails: LocationDetails
)