package com.example.weatherapp.data.weather.repository

import com.example.weatherapp.data.weather.mappers.toWeatherDataMap
import com.example.weatherapp.data.weather.remote.WeatherApi
import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.domain.weather.repository.WeatherRepository
import com.example.weatherapp.utils.ResultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRepository {

    override suspend fun getCurrentWeatherData(
        lat: Double,
        lon: Double
    ): Flow<ResultData<WeatherData>> {
        return flow {
            emit(ResultData.Loading())
            val currentWeatherData = weatherApi.getCurrentWeather(
                latitude = lat,
                longitude = lon
            )
            val resultData = if (currentWeatherData == null) {
                ResultData.Failed()
            } else {
                ResultData.Success(
                    currentWeatherData.toWeatherDataMap()
                )
            }
            emit(resultData)
        }.catch {
            it.printStackTrace()
            emit(ResultData.Failed())
        }
    }
}