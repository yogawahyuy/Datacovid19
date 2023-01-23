package com.systudio.datacovid19.view.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentBarchartBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.utils.MyAxisValueFormatter
import com.systudio.datacovid19.utils.marker.BarChartMarkerView
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.activity_bar_chart.barchart
import kotlinx.android.synthetic.main.fragment_barchart.*

/**
 * A simple [Fragment] subclass.
 * Use the [BarchartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BarchartFragment : Fragment() {

    private var _binding : FragmentBarchartBinding? = null
    private val binding get() = _binding!!
    lateinit var myTextView: ArrayList<TextView>
    //private lateinit var ld: List<ListData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBarchartBinding.inflate(inflater,container,false)
        initVm()
        return binding.root
    }


    private fun initVm(){
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(requireActivity()) {
            if (it != null) {
                //setupBarchart(it)
                //ld = it
                dataProses(it)
            }else{
                binding.cardviewBarchart.visibility = View.GONE
                binding.nointernet.relNointernet.visibility = View.VISIBLE
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupBarChart(entries : List<BarEntry>,label : List<String>){
        val barMarker = BarChartMarkerView(requireActivity(),R.layout.custom_marker_view,label as ArrayList<String>)
        val dataSet = BarDataSet(entries,"")

        dataSet.setDrawValues(false)
        val barChart = binding.barchartFra
        barChart.apply {
            description.isEnabled = false
            data = BarData(dataSet)
            animateXY(200,500)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = false
            marker = barMarker
        }
        val xAxis = binding.barchartFra.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 2f
            setDrawGridLines(false)
            valueFormatter = IndexAxisValueFormatter(label)
        }

    }

    private fun dataProses(listData: List<ListData>){
        myTextView = ArrayList()
        val barEntriesList = ArrayList<BarEntry>()
        val label = ArrayList<String>()
        val param = LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        param.setMargins(25,10,10,10)
        for (i in 0..5){
            barEntriesList.add(BarEntry(i.toFloat(),listData.get(i).jumlah_kasis.toFloat()))
            label.add(listData.get(i).key)
            myTextView.add(TextView(requireContext()))
            myTextView[i].text = listData.get(i).jumlah_kasis.toString()
            myTextView[i].textSize = 8f
            myTextView[i].typeface = Typeface.DEFAULT
            myTextView[i].setTextColor(Color.BLACK)
            myTextView.get(i).layoutParams = param
            binding.linTopValue.addView(myTextView[i])
        }
        setupBarChart(barEntriesList,label)
    }

}