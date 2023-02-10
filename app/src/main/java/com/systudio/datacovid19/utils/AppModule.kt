package com.systudio.datacovid19.utils

import com.systudio.datacovid19.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //const val baseURL = "https://data.covid19.go.id/public/api/"
    const val baseURL = "https://api.jsonbin.io/v3/b/"

    @Provides
    @Singleton
    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
        val loggingInterceptor =  HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }else OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideAppService(retrofit: Retrofit):ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun buildRetrofitService(okHttpClient: OkHttpClient):Retrofit =
        Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
}