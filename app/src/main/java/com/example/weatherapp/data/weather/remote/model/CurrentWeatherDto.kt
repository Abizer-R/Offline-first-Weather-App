package com.example.weatherapp.data.weather.remote.model

data class CurrentWeatherDto(
    val coord: Coord,
    val timezone: Int,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val sys: Sys,
    val name: String
//    val dt: Int,
//    val visibility: Int,
//    val rain: Rain,
//    val clouds: Clouds
) {
    data class Coord(
        val lat: Double,
        val lon: Double
    )

    data class Weather(
        val description: String,
        val icon: String,
        val id: Int,
        val main: String
    )

    data class Main(
        val temp: Double,
        val temp_min: Double,
        val temp_max: Double,
        val pressure: Int,
        val humidity: Int,
    )

    data class Wind(
        val deg: Int,
        val gust: Double,
        val speed: Double
    )


    data class Sys(
        val country: String,
        val id: Int,
    )


//    data class Clouds(
//        val all: Int
//    )

//    data class Rain(
//        val `1h`: Double
//    )
//

}