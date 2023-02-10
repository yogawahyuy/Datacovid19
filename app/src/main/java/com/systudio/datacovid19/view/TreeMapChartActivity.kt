package com.systudio.datacovid19.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.TreeDataEntry
import com.anychart.charts.TreeMap
import com.systudio.datacovid19.R
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.anychart.APIlib
import com.anychart.enums.TreeFillingMethod
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.systudio.datacovid19.utils.GetJSONDataFromAsset
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TreeMapChartActivity : AppCompatActivity() {

    private lateinit var listData: List<ListData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree_map_chart)
        initVM()
//        val jsonFile= GetJSONDataFromAsset().getData(applicationContext,"prov.json")
//        Log.d("jsonoffline", "onCreate: "+jsonFile)
//
//        val gson = Gson()
//        val listDataProv = object : TypeToken<List<ListData>>(){}.type
//        listData = gson.fromJson(jsonFile,listDataProv)

    }

    private fun initVM(){
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(this) {
            if (it != null) {
                listData = it
                dataProses()
            }
        }
    }
    private fun setupTreemap(dataEntry: List<DataEntry>){
        val treeMapChart : AnyChartView = findViewById(R.id.treemap_chart)
        val treeMap: TreeMap = AnyChart.treeMap()
        treeMap.data(dataEntry,TreeFillingMethod.AS_TABLE)
        treeMapChart.setChart(treeMap)
        APIlib.getInstance().setActiveAnyChartView(treeMapChart)

    }

    private fun dataProses(){
        val treeDataEntry = ArrayList<TreeDataEntry>()
        treeDataEntry.add(CustomTreeDataEntry("Kasus COVID-19",null,"Kasus COVID-19"))
        for (i in 0..5){
            //treeDataEntry.add(TreeDataEntry(listData.get(i).key,listData.get(i).key,listData.get(i).jumlah_kasis))
            treeDataEntry.add(CustomTreeDataEntry(listData.get(i).key,"Kasus COVID-19",listData.get(i).key,listData.get(i).jumlah_kasis))
            treeDataEntry.add(CustomTreeDataEntry("Jumlah Sembuh",listData.get(i).key,"Jumlah Sembuh",listData.get(i).jumlah_sembuh))
            treeDataEntry.add(CustomTreeDataEntry("Jumlah Dirawat",listData.get(i).key,"Jumlah Dirawat",listData.get(i).jumlah_dirawat))
            treeDataEntry.add(CustomTreeDataEntry("Jumlah Meninggal",listData.get(i).key,"Jumlah Meninggal",listData.get(i).jumlah_meninggal))
        }
        setupTreemap(treeDataEntry)

    }

    class CustomTreeDataEntry : TreeDataEntry {
        constructor(id:String,parent:String,product:String,value: Int) : super(id,parent,value) {
            setValue("product",product)
        }
        constructor(id:String, parent: String?, product: String): super(id,parent){
            setValue("product",product)
        }
    }
}