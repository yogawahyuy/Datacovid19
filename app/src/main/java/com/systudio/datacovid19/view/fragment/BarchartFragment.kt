package com.systudio.datacovid19.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentBarchartBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
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
                setupBarchart(it)
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupBarchart(listData :List<ListData>){
        val barEntriesList = ArrayList<BarEntry>()

        for (i in 0..5 ){
            Log.d("setupbarchart", "setupBarchart: "+listData.get(i).jumlah_kasis)
            barEntriesList.add(BarEntry(i.toFloat(),listData.get(i).jumlah_kasis.toFloat()))
        }
        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        val barDataSet = BarDataSet(barEntriesList,"Provinsi")
        barDataSet.color = Color.BLUE
        binding.barchartFra.description.isEnabled = false
        binding.barchartFra.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barchartFra.data = BarData(barDataSet)
        binding.barchartFra.animateXY(100,500)
        binding.barchartFra.xAxis?.valueFormatter = IndexAxisValueFormatter(label)
        binding.barchartFra.xAxis.granularity = 2f
        binding.barchartFra.xAxis.setDrawGridLines(false)
        binding.barchartFra.axisLeft.setDrawGridLines(false)

        val legend = binding.barchartFra.legend
        legend.isEnabled = false
        val rightAxis = binding.barchartFra.axisRight
        rightAxis.isEnabled = false
        val marker = BarChartMarkerView(requireActivity(),R.layout.custom_marker_view,label)
        binding.barchartFra.marker = marker

        //barChart.invalidate()

    }

}