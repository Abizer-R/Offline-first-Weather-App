package com.example.weatherapp.data.weather.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.weather.local.dao.WeatherDao
import com.example.weatherapp.data.weather.local.model.CurrentWeatherDB

@Database(
    entities = [CurrentWeatherDB::class],
    version = 1
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val weatherDao: WeatherDao

    companion object {
        const val DATABASE_NAME = "weather_db"
    }
}