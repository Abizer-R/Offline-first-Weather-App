package com.example.weatherapp.data.weather.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.weather.local.model.CurrentWeatherDB
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherDB: CurrentWeatherDB)

    @Delete
    suspend fun deleteWeatherData(weatherDB: CurrentWeatherDB)

    @Query("SELECT * FROM current_weather_data_table")
    fun getCurrentWeatherData(): Flow<List<CurrentWeatherDB>>
}