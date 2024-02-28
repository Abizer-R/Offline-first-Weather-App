package com.example.weatherapp.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.weatherapp.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object AppUtils {

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                try {
                    val capabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    if (capabilities != null) {
                        when {
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                                return true
                            }

                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                                return true
                            }

                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                                return true
                            }
                        }
                    }
                } catch (e: Exception) {
                    //not handling
                }
            }

            else -> {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    return true
                }
            }
        }
        return false
    }

    fun getRelativeDateTimeString(
        dateTime: LocalDateTime,
        resources: Resources
    ) : String {

        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return when {
            checkToday(dateTime) -> {
                "${resources.getString(R.string.today)} ${dateTime.format(timeFormatter)}"
            }

            checkYesterday(dateTime) -> {
                "${resources.getString(R.string.yesterday)} ${dateTime.format(timeFormatter)}"
            }

            else -> {
                "${dateTime.format(dateFormatter)} ${dateTime.format(timeFormatter)}"
            }
        }
    }


    fun checkToday(
        date: LocalDateTime
    ): Boolean {
        return checkSameDay(date, LocalDateTime.now())
    }

    fun checkYesterday(
        date: LocalDateTime
    ): Boolean {
        return checkSameDay(date, LocalDateTime.now().minusDays(1))
    }


    fun checkSameDay(
        date1: LocalDateTime,
        date2: LocalDateTime
    ): Boolean {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val date1Str = date1.format(dateTimeFormatter)
        val date2Str = date1.format(dateTimeFormatter)
        return date1Str == date2Str
    }

    fun getWeatherIconDrawable(
        iconCode: String,
        context: Context
    ): Drawable? {
        return when(iconCode) {
            "01d" -> ContextCompat.getDrawable(context, R.drawable.ic_01d)
            "01n" -> ContextCompat.getDrawable(context, R.drawable.ic_01n)
            "02d" -> ContextCompat.getDrawable(context, R.drawable.ic_02d)
            "02n" -> ContextCompat.getDrawable(context, R.drawable.ic_02n)
            "03d" -> ContextCompat.getDrawable(context, R.drawable.ic_03d)
            "03n" -> ContextCompat.getDrawable(context, R.drawable.ic_03n)
            "04d" -> ContextCompat.getDrawable(context, R.drawable.ic_04d)
            "04n" -> ContextCompat.getDrawable(context, R.drawable.ic_04n)
            "09d" -> ContextCompat.getDrawable(context, R.drawable.ic_09d)
            "09n" -> ContextCompat.getDrawable(context, R.drawable.ic_09n)
            "10d" -> ContextCompat.getDrawable(context, R.drawable.ic_10d)
            "10n" -> ContextCompat.getDrawable(context, R.drawable.ic_10n)
            "11d" -> ContextCompat.getDrawable(context, R.drawable.ic_11d)
            "11n" -> ContextCompat.getDrawable(context, R.drawable.ic_11n)
            "13d" -> ContextCompat.getDrawable(context, R.drawable.ic_13d)
            "13n" -> ContextCompat.getDrawable(context, R.drawable.ic_13n)
            "50d" -> ContextCompat.getDrawable(context, R.drawable.ic_50d)
            "50n" -> ContextCompat.getDrawable(context, R.drawable.ic_50n)

            else -> null
        }
    }


    fun getOneDigitDecimal(
        number: Double
    ): String {
        return String.format("%.1f", number)
    }

//    fun Geocoder.getAddress(
//        latitude: Double,
//        longitude: Double,
//        address: (android.location.Address?) -> Unit
//    ) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            getFromLocation(latitude, longitude,1) {
//                address(it.firstOrNull())
//            }
//        } else {
//            try {
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                address(null)
//            }
//        }
//    }
}