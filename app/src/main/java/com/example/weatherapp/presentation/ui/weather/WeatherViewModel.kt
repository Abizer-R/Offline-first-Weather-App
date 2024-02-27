package com.example.weatherapp.presentation.ui.weather

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.domain.weather.repository.WeatherRepository
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

    private val _weatherDataLiveData = MutableLiveData<ResultData<WeatherData>>(ResultData.Loading())
    val weatherDataLiveData: LiveData<ResultData<WeatherData>>
        get() = _weatherDataLiveData

    fun fetchWeatherInfo(
        location: Location
    ) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeatherData(
                lat = location.latitude,
                lon = location.longitude
            ).onEach {
                Log.e("TESTING", "fetchWeatherInfo: result = $it")
                _weatherDataLiveData.postValue(it)
            }.collect()
        }
    }
}