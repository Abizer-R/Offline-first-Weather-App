package com.example.weatherapp.data.weather.mappers

import com.example.weatherapp.data.weather.model.CurrentWeatherDto
import com.example.weatherapp.domain.weather.model.WeatherData
import java.time.LocalDateTime

fun CurrentWeatherDto.toWeatherDataMap() : WeatherData {
    return WeatherData(
        time = LocalDateTime.now(),
        temperatureCelsius = main.temp,
        desc = weather.getOrNull(0)?.description ?: "",
        iconCode = weather.getOrNull(0)?.icon ?: "",
        pressure = main.pressure.toDouble(),
        humidity = main.humidity.toDouble(),
        windSpeed = wind.speed,
        latitude = coord.lat,
        longitude = coord.lon,
        name = name,
        country = sys.country
    )
}