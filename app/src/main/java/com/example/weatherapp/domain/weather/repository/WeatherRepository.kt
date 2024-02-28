package com.example.weatherapp.domain.weather.repository

import com.example.weatherapp.data.weather.local.model.CurrentWeatherDB
import com.example.weatherapp.data.weather.remote.model.GeocoderResponseItem
import com.example.weatherapp.domain.weather.model.LocationDetails
import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.utils.ResultData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getWeatherDataFromDB(): Flow<List<WeatherData>>

    suspend fun getCurrentWeatherDataFromRemote(
        lat: Double,
        lon: Double
    ): Flow<ResultData<WeatherData>>

    suspend fun getCoordinatesFromCityName(
        cityName: String
    ): Flow<ResultData<LocationDetails>>

    suspend fun insertWeatherDataInDB(weatherDataDB: CurrentWeatherDB)
}