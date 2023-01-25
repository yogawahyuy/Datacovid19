package com.systudio.datacovid19.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.systudio.datacovid19.model.ListData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val mainRepository: MainRepository):ViewModel() {
    var liveDataAll: MutableLiveData<List<ListData>>
    init {
        liveDataAll= MutableLiveData()
    }
    fun fetchLiveData():MutableLiveData<List<ListData>>{
        return liveDataAll
    }
    fun fetchAllData(){
        mainRepository.getData(liveDataAll)
    }
}