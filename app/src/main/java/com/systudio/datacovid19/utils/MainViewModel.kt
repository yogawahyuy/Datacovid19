package com.systudio.datacovid19.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systudio.datacovid19.model.ListData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val mainRepository: MainRepository):ViewModel() {
    val apikey = "$2b$10$"+"p3IK6Cz4AIabGTbXF3EMVOxKcsX8uLnh2oscG30pID0go4eIjfg9K"
    var liveDataAll: MutableLiveData<List<ListData>>
    init {
        liveDataAll= MutableLiveData()
    }
    fun fetchLiveData():MutableLiveData<List<ListData>>{
        mainRepository.getData(apikey,liveDataAll)
        return liveDataAll
    }
    fun fetchAllData(){
        mainRepository.getData(liveDataAll)
    }
}