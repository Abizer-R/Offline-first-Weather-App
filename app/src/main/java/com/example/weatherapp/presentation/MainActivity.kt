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
import com.example.weatherapp.utils.hide
import com.example.weatherapp.utils.invisible
import com.example.weatherapp.utils.show
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

        initUserInterface()
        addObservers()
        weatherViewModel.fetchWeatherInfo(
            latitude = 22.719568,
            longitude = 75.857727
        )
    }

    private fun initUserInterface() {
        with (viewBinding.cvWeatherDetails) {
            btnRefresh.setOnClickListener {
                weatherViewModel.fetchWeatherInfo(
                    latitude = 22.719568,
                    longitude = 75.857727
                )
            }
        }
    }

    private fun addObservers() {
        weatherViewModel.weatherDataLiveData.observe(this) {
            when (it) {
                is ResultData.Loading -> {
                    updateRefreshing(isRefreshing = true)
                }

                is ResultData.Success -> {
                    updateRefreshing(isRefreshing = false)
                    updateWeatherDetails(it.data)
                }

                is ResultData.Failed -> {
                    updateRefreshing(isRefreshing = false)
                    viewBinding.cvWeatherDetails.shimmerInvisibleGroup.invisible()
                    viewBinding.cvWeatherDetails.tvError.show()
                }
            }
        }
    }

    private fun updateRefreshing(isRefreshing: Boolean) {
        viewBinding.cvWeatherDetails.tvError.hide()
        if (isRefreshing) {
            viewBinding.shimmerLayout.show()
            viewBinding.shimmerLayout.startShimmer()
            viewBinding.cvWeatherDetails.apply {
                shimmerInvisibleGroup.invisible()
                progressBar.show()
                btnRefresh.invisible()
            }

        } else {
            viewBinding.shimmerLayout.hide()
            viewBinding.shimmerLayout.stopShimmer()
            viewBinding.cvWeatherDetails.apply {
                shimmerInvisibleGroup.show()
                progressBar.hide()
                btnRefresh.show()
            }

        }
    }

    private fun updateWeatherDetails(data: WeatherData) {
        with(viewBinding.cvWeatherDetails) {
            tvUpdatedOn.text = WeatherDetailsUtil.getRelativeDateTimeString(data.time, resources)
            tvLocation.text = "${data.name}, ${data.country}"
            tvDescription.text = data.desc
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