package com.systudio.datacovid19.utils

import android.content.Context
import java.io.IOException

class GetJSONDataFromAsset {
    fun getData(context: Context,filename:String): String?{
        val jsonString : String
        try {
            jsonString = context.assets.open(filename).bufferedReader().use {
                it.readText()
            }
        }catch (ioException:IOException){
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}