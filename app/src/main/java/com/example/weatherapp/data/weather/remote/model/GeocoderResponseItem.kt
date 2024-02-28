package com.example.weatherapp.data.weather.remote.model

data class GeocoderResponseItem(
    val country: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String
)