package com.example.weatherapp.data.weather.repository

import android.util.Log
import com.example.weatherapp.data.weather.local.dao.WeatherDao
import com.example.weatherapp.data.weather.local.model.CurrentWeatherDB
import com.example.weatherapp.data.weather.mappers.toWeatherDataMap
import com.example.weatherapp.data.weather.remote.WeatherApi
import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.domain.weather.repository.WeatherRepository
import com.example.weatherapp.utils.ResultData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    override suspend fun getWeatherDataFromDB(): Flow<List<WeatherData>> {
        return flow {
            weatherDao.getCurrentWeatherData().onEach { weatherDbList ->
                Log.e("TESTING", "REPO - getWeatherDataFromDB: data = $weatherDbList", )
                val mappedList = weatherDbList.map { weatherDb ->
                    weatherDb.toWeatherDataMap()
                }
                emit(mappedList)
            }.collect()
        }
//        return weatherDao.getCurrentWeatherData()
    }

    override suspend fun getCurrentWeatherDataFromRemote(
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

    override suspend fun insertWeatherDataInDB(weatherDataDB: CurrentWeatherDB) {
        weatherDao.insertWeatherData(weatherDataDB)
    }
}