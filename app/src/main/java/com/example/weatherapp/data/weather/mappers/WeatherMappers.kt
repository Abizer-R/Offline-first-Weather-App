package com.example.weatherapp.data.weather.mappers

import com.example.weatherapp.data.weather.local.model.CurrentWeatherDB
import com.example.weatherapp.data.weather.remote.model.CurrentWeatherDto
import com.example.weatherapp.data.weather.remote.model.GeocoderResponseItem
import com.example.weatherapp.domain.weather.model.LocationDetails
import com.example.weatherapp.domain.weather.model.WeatherData
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

fun CurrentWeatherDto.toWeatherDataMap() : WeatherData {
    return WeatherData(
        time = LocalDateTime.now(),
        temperatureCelsius = main.temp,
        desc = weather.getOrNull(0)?.description ?: "",
        iconCode = weather.getOrNull(0)?.icon ?: "",
        pressure = main.pressure.toDouble(),
        humidity = main.humidity.toDouble(),
        windSpeed = wind.speed,
        locationDetails = LocationDetails(
            latitude = coord.lat,
            longitude = coord.lon,
            name = name,
            country = sys.country
        )
    )
}

fun CurrentWeatherDB.toWeatherDataMap() : WeatherData {
    return WeatherData(
        isCurrentLocation = isCurrentLocation,
        time = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp),
            TimeZone.getDefault().toZoneId()
        ),
        temperatureCelsius = temperatureCelsius,
        desc = desc,
        iconCode = iconCode,
        pressure = pressure,
        windSpeed = windSpeed,
        humidity = humidity,
        locationDetails = LocationDetails(
            latitude = latitude,
            longitude = longitude,
            name = name,
            country = country
        )
    )
}

fun CurrentWeatherDto.toCurrentWeatherDB() : CurrentWeatherDB {
    return CurrentWeatherDB(
        timestamp = Calendar.getInstance().timeInMillis,
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

fun WeatherData.toCurrentWeatherDB(
    id: Int? = null,
    isCurrentLocation: Boolean? = null
) : CurrentWeatherDB {
    var obj = CurrentWeatherDB(
        timestamp = time.atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli(),
        temperatureCelsius = temperatureCelsius,
        desc = desc,
        iconCode = iconCode,
        pressure = pressure,
        windSpeed = windSpeed,
        humidity = humidity,
        latitude = locationDetails.latitude,
        longitude = locationDetails.longitude,
        name = locationDetails.name,
        country = locationDetails.country
    )
    id?.let {
        obj = obj.copy(
            id = it
        )
    }
    isCurrentLocation?.let {
        obj = obj.copy(
            isCurrentLocation = isCurrentLocation
        )
    }
    return obj
}

fun GeocoderResponseItem.toLocationDetails(): LocationDetails {
    return LocationDetails(
        latitude = lat,
        longitude = lon,
        name = name,
        country = country
    )
}