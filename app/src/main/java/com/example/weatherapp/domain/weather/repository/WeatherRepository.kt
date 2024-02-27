package com.example.weatherapp.domain.weather.repository

import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.utils.ResultData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getCurrentWeatherData(
        lat: Double,
        lon: Double
    ) : Flow<ResultData<WeatherData>>
}