package com.systudio.datacovid19.model

import com.google.gson.annotations.SerializedName

data class Recording(
    @SerializedName("record"   ) var record   : Record?   = Record(),
)

data class Record (
    @SerializedName("last_date"      ) var lastDate      : String?             = null,
    @SerializedName("current_data"   ) var currentData   : Double?             = null,
    @SerializedName("missing_data"   ) var missingData   : Double?             = null,
    @SerializedName("tanpa_provinsi" ) var tanpaProvinsi : Int?                = null,
    @SerializedName("list_data"      ) var listData      : ArrayList<ListData> = arrayListOf()

)
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
