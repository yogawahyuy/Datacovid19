package com.systudio.datacovid19.utils

import com.systudio.datacovid19.model.DataCovidModel
import com.systudio.datacovid19.model.Record
import com.systudio.datacovid19.model.Recording
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("prov.json")
    fun getData():Call<DataCovidModel>
    @GET("63e5c596ace6f33a22da9336")
    fun getDataNew(@Header("X-MASTER-KEY")apikey:String):Call<Recording>
}