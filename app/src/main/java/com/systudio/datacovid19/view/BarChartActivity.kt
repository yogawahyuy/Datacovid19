package com.systudio.datacovid19.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.systudio.datacovid19.R
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*

@AndroidEntryPoint
class BarChartActivity : AppCompatActivity() {
    //lateinit var barChart : BarChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)
        setupVM()
    }

    private fun setupVM(){
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(this, Observer {
            if (it!=null){
                setupBarchart(it)
            }
        })
        viewModel.fetchAllData()
    }

    private fun setupBarchart(listData :List<ListData>){
        val barEntriesList = ArrayList<BarEntry>()

        for (i in 0..5 ){
            Log.d("setupbarchart", "setupBarchart: "+listData.get(i).jumlah_kasis)
                barEntriesList.add(BarEntry(i.toFloat()+1,listData.get(i).jumlah_kasis.toFloat()))
        }
        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        val barDataSet = BarDataSet(barEntriesList,"Provinsi")
        barDataSet.color = Color.BLUE
        barchart.description.isEnabled = false
        barchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barchart.data = BarData(barDataSet)
        barchart.animateXY(100,500)
        barchart.xAxis?.valueFormatter = IndexAxisValueFormatter(label)
        //barChart.invalidate()

    }

}