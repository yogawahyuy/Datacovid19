package com.systudio.datacovid19.model

import com.google.gson.annotations.SerializedName

data class DataCovidModel(
    @SerializedName("last_date")
    val lastDate:String,
    @SerializedName("list_data")
    var listData : ArrayList<ListData> = arrayListOf()
)
data class ListData(
    @SerializedName("key")
    val key:String,
    @SerializedName("jumlah_kasus")
    val jumlah_kasis:Int,
    @SerializedName("jumlah_sembuh")
    val jumlah_sembuh:Int,
    @SerializedName("jumlah_meninggal")
    val jumlah_meninggal:Int,
    @SerializedName("jumlah_dirawat")
    val jumlah_dirawat:Int
)
