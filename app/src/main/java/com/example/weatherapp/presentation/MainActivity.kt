package com.example.weatherapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.presentation.ui.weather.WeatherViewModel
import com.example.weatherapp.utils.ResultData
import com.example.weatherapp.utils.WeatherDetailsUtil
import dagger.hilt.android.AndroidEntryPoint
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val viewBinding get() = requireNotNull(_binding)

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        weatherViewModel.fetchWeatherInfo(
            latitude = 22.719568,
            longitude = 75.857727
        )

        weatherViewModel.weatherDataLiveData.observe(this) {
            when (it) {
                is ResultData.Loading -> {
                    // TODO
                }

                is ResultData.Success -> {
                    updateWeatherDetails(it.data)
                }

                is ResultData.Failed -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateWeatherDetails(data: WeatherData) {
        with(viewBinding) {
            tvUpdatedOn.text = WeatherDetailsUtil.getRelativeDateTimeString(data.time, resources)
            tvLocation.text = "${data.name}, ${data.country}"
            tvDescription.text = data.desc
            // limit decimal to 1 digit and use string resource
            tvTemp.text = getString(R.string.temperature_with_symbol, getOneDigitDecimal(data.temperatureCelsius))
            tvPressure.text = getString(R.string.pressure_with_symbol, data.pressure.toInt())
            tvHumidity.text = getString(R.string.humidity_with_symbol, data.humidity.toInt())
            tvWindSpeed.text = getString(R.string.windspeed_with_symbol, getOneDigitDecimal(data.windSpeed))

            val iconDrawable =
                WeatherDetailsUtil.getWeatherIconDrawable(data.iconCode, this@MainActivity)
                    ?: ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_01d)
            ivWeather.setImageDrawable(iconDrawable)
        }
    }

    private fun getOneDigitDecimal(
        number: Double
    ): String {
        return String.format("%.1f", number)
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}