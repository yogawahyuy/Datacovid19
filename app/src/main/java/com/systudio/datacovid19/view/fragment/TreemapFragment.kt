package com.systudio.datacovid19.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.anychart.APIlib
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.TreeDataEntry
import com.anychart.charts.TreeMap
import com.anychart.enums.*
import com.systudio.datacovid19.R
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.view.TreeMapChartActivity


/**
 * A simple [Fragment] subclass.
 * Use the [TreemapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TreemapFragment : Fragment() {

    private val viewModel: MainViewModel by hiltNavGraphViewModels(R.id.main_navigation)
    private lateinit var listData: List<ListData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treemap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVM(view)
        Toast.makeText(requireContext(),"OnViewCreated treemap done created", Toast.LENGTH_LONG).show()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupTreemap(view: View,dataEntry: List<DataEntry>){
        val treeMapChart : AnyChartView = view.findViewById(R.id.any_chart_treemap)
        val treeMap: TreeMap = AnyChart.treeMap()
        treeMap.data(dataEntry,TreeFillingMethod.AS_TABLE)
//        treeMap.apply {
//            data(dataEntry,TreeFillingMethod.AS_TABLE)
           // padding(10,10,10,20)
//            maxDepth(2)
//            hovered().fill("#bdbdbd",1)
//            selectionMode(SelectionMode.NONE)
//            legend().enabled(true)
           // legend().padding(0,0,0,20)
//                .position(Orientation.BOTTOM)
//                .align(Align.CENTER)
//                .itemsLayout(LegendLayout.HORIZONTAL)
       // }
        //treeMap.colorScale().colors(arrayOf(Color.GRAY.toString(),Color.GREEN.toString(),Color.BLUE.toString(),Color.RED.toString()))

        treeMapChart.setChart(treeMap)
        APIlib.getInstance().setActiveAnyChartView(treeMapChart)


    }

    private fun initVM(view: View){
        viewModel.fetchLiveData().observe(requireActivity()) {
            if (it != null) {
                listData = it
                dataProses(view)
            }
        }
    }

    private fun dataProses(view: View){
        val treeDataEntry = ArrayList<TreeDataEntry>()
        val label = ArrayList<String>()
        treeDataEntry.add(CustomTreeDataEntry("Kasus COVID-19",null,"Kasus COVID-19"))
        for (i in 0..5) {
            treeDataEntry.add(CustomTreeDataEntry(listData.get(i).key,"Kasus COVID-19",listData.get(i).key,listData.get(i).jumlah_kasis))
            treeDataEntry.add(CustomTreeDataEntry("Jumlah Sembuh",listData.get(i).key,"Jumlah Sembuh",listData.get(i).jumlah_sembuh))
            treeDataEntry.add(CustomTreeDataEntry("Jumlah Dirawat",listData.get(i).key,"Jumlah Dirawat",listData.get(i).jumlah_dirawat))
            treeDataEntry.add(CustomTreeDataEntry("Jumlah Meninggal",listData.get(i).key,"Jumlah Meninggal",listData.get(i).jumlah_meninggal))
        }
        setupTreemap(view,treeDataEntry)

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