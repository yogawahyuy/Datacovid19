package com.systudio.datacovid19.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.systudio.datacovid19.model.DataCovidModel
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.model.Record
import com.systudio.datacovid19.model.Recording
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MainRepository @Inject constructor(val apiService: ApiService) {
    fun getData(liveData: MutableLiveData<List<ListData>>){
        val call : Call<DataCovidModel> = apiService.getData()
        call.enqueue(object : Callback<DataCovidModel>{
            override fun onResponse(
                call: Call<DataCovidModel>,
                response: Response<DataCovidModel>,
            ) {
                liveData.postValue(response.body()?.listData)
            }

            override fun onFailure(call: Call<DataCovidModel>, t: Throwable) {
                liveData.postValue(null)
            }

        })
    }
    fun getData(apikey:String,liveData: MutableLiveData<List<ListData>>){
        val call : Call<Recording> = apiService.getDataNew(apikey)
        call.enqueue(object : Callback<Recording>{
            override fun onResponse(
                call: Call<Recording>,
                response: Response<Recording>,
            ) {
                liveData.postValue(response.body()?.record?.listData)
                Log.d("mainrepository", "onResponse: "+response.body()?.record?.listData?.size)
            }

            override fun onFailure(call: Call<Recording>, t: Throwable) {
                liveData.postValue(null)
            }

        })
    }
}