package com.example.weatherapp.di

import com.example.weatherapp.data.weather.remote.WeatherApi
import com.example.weatherapp.utils.Constant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitClient(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Constant.BASE_URL)
    }

    @Provides
    @Singleton
    fun providesWeatherApi(
        okHttpClient: OkHttpClient,
        retrofitBuilder: Retrofit.Builder
    ): WeatherApi {
        return retrofitBuilder
            .client(okHttpClient)
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()

        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        if (BuildConfig.DEBUG) {
//            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        } else {
//            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
//        }
        return loggingInterceptor

    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
        return okHttpClient.build()
    }

}