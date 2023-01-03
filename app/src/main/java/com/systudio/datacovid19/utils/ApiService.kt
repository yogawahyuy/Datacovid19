package com.systudio.datacovid19.utils

import com.systudio.datacovid19.model.DataCovidModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("prov.json")
    fun getData():Call<DataCovidModel>
}