package com.example.weatherapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.domain.weather.model.WeatherData
import com.example.weatherapp.presentation.ui.weather.WeatherAdapter
import com.example.weatherapp.presentation.ui.weather.WeatherViewModel
import com.example.weatherapp.utils.ResultData
import com.example.weatherapp.utils.AppUtils
import com.example.weatherapp.utils.hide
import com.example.weatherapp.utils.invisible
import com.example.weatherapp.utils.show
import com.example.weatherapp.utils.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val viewBinding get() = requireNotNull(_binding)

    private val weatherViewModel: WeatherViewModel by viewModels()

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val weatherAdapter: WeatherAdapter by lazy {
        WeatherAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initUserInterface()
        addObservers()

        fetchLocationDetailsFromRemote(failToast = true)

    }

    private fun initUserInterface() {
        weatherViewModel.startObservingDB()
        with (viewBinding) {

            rvWeatherDetails.layoutManager = LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.VERTICAL, false
            )
            rvWeatherDetails.adapter = weatherAdapter

            btnRefresh.setOnClickListener {
                fetchLocationDetailsFromRemote(failToast = true)
            }

            swipeRefreshLayout.setOnRefreshListener {
                fetchLocationDetailsFromRemote(failToast = true)
            }
        }
    }

    private fun addObservers() {
        weatherViewModel.weatherDataLiveData.observe(this) {
            when (it) {
                is ResultData.Loading -> {
                    if (weatherViewModel.weatherData.isEmpty()) {
                        updateRefreshing(isRefreshing = true)
                    }
                }

                is ResultData.Success -> {
                    updateRefreshing(isRefreshing = false)
                    if (weatherViewModel.weatherData.isNotEmpty()) {
                        weatherAdapter.submitList(weatherViewModel.weatherData)
                        weatherAdapter.notifyDataSetChanged()
                    } else {
                        fetchLocationDetailsFromRemote(failToast = false, errorLayout = true)
                    }
                }

                is ResultData.Failed -> {
                    showErrorLayout(
                        errorText = getString(R.string.error_text)
                    )
                }
            }
        }
    }

    private fun updateRefreshing(isRefreshing: Boolean) {
        viewBinding.errorLayout.hide()
        if (isRefreshing) {
            viewBinding.shimmerLayout.show()
            viewBinding.shimmerLayout.startShimmer()


        } else {
            viewBinding.shimmerLayout.hide()
            viewBinding.shimmerLayout.stopShimmer()
            viewBinding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showErrorLayout(errorText: String) {
        updateRefreshing(isRefreshing = false)
        viewBinding.rvWeatherDetails.hide()
        viewBinding.errorLayout.show()
        viewBinding.tvError.text = errorText
    }

    private fun fetchLocationDetailsFromRemote(
        failToast: Boolean,
        errorLayout: Boolean = false
    ) {
        if (AppUtils.isNetworkAvailable(this).not()) {
            if (failToast) {
                this.toast(getString(R.string.check_internet_connection))
            }
            if (errorLayout) {
                showErrorLayout(
                    errorText = getString(R.string.check_internet_connection)
                )
            }
            updateRefreshing(isRefreshing = false)
            return
        }

        updateRefreshing(isRefreshing = true)
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }


    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var isCoarseGranted = false
        var isFineGranted = false
        permissions.forEach {
            when (it.key) {
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    isCoarseGranted = it.value
                }

                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    isFineGranted = it.value
                }
            }
        }

        if (isCoarseGranted.not()) {
            showErrorLayout(getString(R.string.location_permission_denied))
            this.toast(getString(R.string.need_location_permission_to_fetch))
            return@registerForActivityResult
        }

        if (isFineGranted.not()) {
            this.toast(getString(R.string.location_might_not_be_exact))
        }
        getLocationIfGpsEnabled()
    }

    private fun getLocationIfGpsEnabled() {

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isGpsEnabled.not()) {
            showErrorLayout(getString(R.string.please_enable_gps))
            return
        }
        getLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }

        lifecycleScope.launch {
            // Fetch last location cuz it is faster
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                location?.let {
                    fetchWeatherInfo(it, 0)
                }
            }
        }

        val usePrecision = ContextCompat.checkSelfPermission(
            this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        lifecycleScope.launch {
            // Fetch the current location
            val priority = if (usePrecision) {
                Priority.PRIORITY_HIGH_ACCURACY
            } else {
                Priority.PRIORITY_BALANCED_POWER_ACCURACY
            }
            fusedLocationClient?.getCurrentLocation(
                priority,
                CancellationTokenSource().token
            )?.addOnSuccessListener { location ->
                location?.let {
                    fetchWeatherInfo(it, 0)
                }
            }?.addOnFailureListener {
                it.printStackTrace()
                this@MainActivity.toast(getString(R.string.could_not_fetch_current_location))
            }
        }
    }

    private fun fetchWeatherInfo(location: Location, position: Int) {
        weatherViewModel.fetchWeatherInfo(
            location.latitude,
            location.longitude,
            position
        )
    }


    override fun onDestroy() {
        _binding = null
        fusedLocationClient = null
        super.onDestroy()
    }
}