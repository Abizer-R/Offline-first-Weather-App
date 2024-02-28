package com.example.weatherapp.presentation.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.WeatherDetailsListItemBinding
import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.utils.AppUtils
import com.example.weatherapp.utils.AppUtils.getOneDigitDecimal

class WeatherAdapter() :
    ListAdapter<WeatherData, WeatherAdapter.WeatherViewHolder>(
        object : DiffUtil.ItemCallback<WeatherData>() {
            override fun areItemsTheSame(
                oldItem: WeatherData,
                newItem: WeatherData
            ): Boolean {
                return newItem.locationDetails == oldItem.locationDetails
            }

            override fun areContentsTheSame(
                oldItem: WeatherData,
                newItem: WeatherData
            ): Boolean {
                return newItem == oldItem
            }
        }
    ) {

    inner class WeatherViewHolder(
        val binding: WeatherDetailsListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: WeatherData) {
            val mContext = binding.root.context
            with(binding) {
                tvUpdatedOn.text = AppUtils.getRelativeDateTimeString(data.time, mContext.resources)
                val locationDetails = data.locationDetails
                tvLocation.text = "${locationDetails.name}, ${locationDetails.country}"
                tvDescription.text = data.desc
                tvTemp.text = mContext.getString(
                    R.string.temperature_with_symbol,
                    getOneDigitDecimal(data.temperatureCelsius)
                )
                tvPressure.text = mContext.getString(
                    R.string.pressure_with_symbol, data.pressure.toInt()
                )
                tvHumidity.text = mContext.getString(
                    R.string.humidity_with_symbol, data.humidity.toInt()
                )
                tvWindSpeed.text = mContext.getString(
                    R.string.windspeed_with_symbol, getOneDigitDecimal(data.windSpeed)
                )
                val iconDrawable = AppUtils.getWeatherIconDrawable(data.iconCode, mContext)
                        ?: ContextCompat.getDrawable(mContext, R.drawable.ic_01d)
                ivWeather.setImageDrawable(iconDrawable)

                llCurrentLocation.isVisible = data.isCurrentLocation
            }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            WeatherDetailsListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}