package com.systudio.datacovid19.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.systudio.datacovid19.model.DataCovidModel
import com.systudio.datacovid19.model.ListData
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
}