package com.example.weatherapp.data.weather.remote

import com.example.weatherapp.data.weather.remote.model.CurrentWeatherDto
import com.example.weatherapp.data.weather.remote.model.GeocoderResponseItem
import com.example.weatherapp.utils.Constant
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = Constant.API_KEY
    ): CurrentWeatherDto?

    @GET("geo/1.0/direct")
    suspend fun getCoordinatesFromCityName(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String = Constant.API_KEY
    ): List<GeocoderResponseItem>?
}