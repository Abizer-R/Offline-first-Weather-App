package com.example.weatherapp.data.weather.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather_data_table")
data class CurrentWeatherDB(
    val isCurrentLocation: Boolean = false,
    val timestamp: Long,
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
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)
