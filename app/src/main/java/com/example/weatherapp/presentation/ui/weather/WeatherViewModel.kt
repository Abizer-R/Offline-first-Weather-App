package com.example.weatherapp.presentation.ui.weather

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.weather.mappers.toCurrentWeatherDB
import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.domain.weather.repository.WeatherRepository
import com.example.weatherapp.utils.Constant
import com.example.weatherapp.utils.ResultData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private var _weatherData = mutableListOf<WeatherData>()
    val weatherData: List<WeatherData> get() = _weatherData

    private val _weatherDataLiveData = MutableLiveData<ResultData<Boolean>>(ResultData.Loading())
    val weatherDataLiveData: LiveData<ResultData<Boolean>>
        get() = _weatherDataLiveData

    init {
        viewModelScope.launch {
            weatherRepository.getWeatherDataFromDB().onEach { weatherList ->
                Log.e("TESTING", "VM - getWeatherDataFromDB: data = $weatherList", )
                if (weatherList.isEmpty()) {
                    _weatherDataLiveData.postValue(ResultData.Success(true))
                } else {
                    _weatherData.clear()
                    _weatherData.addAll(weatherList)
                    _weatherDataLiveData.postValue(ResultData.Success(true))
                }
            }.collect()
        }
        getDataForOtherCities()
    }

    fun startObservingDB() = viewModelScope.launch {

    }

    fun fetchWeatherInfo(
        latitude: Double,
        longitude: Double,
        position: Int
    ) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeatherDataFromRemote(
                lat = latitude,
                lon = longitude
            ).onEach {
                Log.e("TESTING", "fetchWeatherInfo: result = $it")
                when (it) {
                    is ResultData.Success -> {
                        weatherRepository.insertWeatherDataInDB(
                            it.data.toCurrentWeatherDB(
                                id = position,
                                isCurrentLocation = position == 0
                            )
                        )
                    }
                    is ResultData.Loading -> {
                        _weatherDataLiveData.postValue(ResultData.Loading())
                    }
                    is ResultData.Failed -> {
                        _weatherDataLiveData.postValue(ResultData.Failed())
                    }
                }
            }.collect()
        }
    }

    fun getDataForOtherCities() {
        Constant.citiesList.forEachIndexed { index, cityName ->
            fetchCityLocationAndWeatherDetails(
                cityName = cityName,
                position = index + 1
            )
        }
    }

    private fun fetchCityLocationAndWeatherDetails(
        cityName: String,
        position: Int
    ) = viewModelScope.launch {
        weatherRepository.getCoordinatesFromCityName(
            cityName = cityName
        ).onEach {
            when (it) {
                is ResultData.Success -> {
                    fetchWeatherInfo(
                        it.data.latitude,
                        it.data.longitude,
                        position
                    )
                }
                else -> {}
            }
        }.collect()
    }
}