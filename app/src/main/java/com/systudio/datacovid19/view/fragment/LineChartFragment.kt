package com.systudio.datacovid19.view.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentLineChartBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.utils.marker.LineChartMarker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.custom_legend_filter.*
import kotlinx.android.synthetic.main.layout_line_chart.*
import kotlinx.android.synthetic.main.layout_line_chart.view.*
import kotlinx.android.synthetic.main.layout_stackbar_chart.*


/**
 * A simple [Fragment] subclass.
 * Use the [LineChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LineChartFragment : Fragment() {

    private var _binding : FragmentLineChartBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLineChartBinding.inflate(layoutInflater,container,false)
        initVm()
        return binding.root
    }

    private fun initVm(){
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(requireActivity()) {
            if (it != null) {

                dataProces(it)
            }else{
                binding.cardviewLinechart.visibility = View.GONE
                binding.nointernet.relNointernet.visibility = View.VISIBLE
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupLineChart(entriesSembuh : List<Entry>,entriesDirawat: List<Entry>,entriesMeninggal: List<Entry>,label: List<String>){
        val dataSetSembuh = LineDataSet(entriesSembuh,"")
        dataSetSembuh.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.GREEN
            circleRadius = 5f
            setCircleColor(Color.GREEN)
        }
        val dataSetDirawat = LineDataSet(entriesDirawat,"")
        dataSetDirawat.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.BLUE
            circleRadius = 5f
            setCircleColor(Color.BLUE)
        }
        val dataSetMeninggal = LineDataSet(entriesMeninggal,"")
        dataSetMeninggal.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.RED
            circleRadius = 5f
            setCircleColor(Color.RED)
        }
        val lineChart = binding.lineChart
        lineChart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            animateXY(200,500)
            axisRight.isEnabled = false
            data = LineData(dataSetSembuh,dataSetDirawat,dataSetMeninggal)
        }
        val xAxis = binding.lineChart.xAxis
        xAxis.apply {
            granularity = 2f
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            valueFormatter = IndexAxisValueFormatter(label)
        }
        val leftAxis = binding.lineChart.axisLeft
        leftAxis.apply {
            leftAxis.axisMinimum = 0f
            setDrawGridLines(false)
        }
        var state = 0
        binding.linChartsembuh.setOnClickListener {
            if (state==0) {
                dataSetSembuh.isVisible = false
                binding.relLayLineChart.lineChart.invalidate()
                binding.sembuhTvline.paintFlags = binding.sembuhTvline.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                state = 1
            }else {
                dataSetSembuh.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.sembuhTvline.paintFlags = binding.sembuhTvline.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                state = 0
            }
        }
        var stateDirawat = 0
        binding.linChartdirawat.setOnClickListener {
            if (stateDirawat==0){
                dataSetDirawat.isVisible = false
                binding.relLayLineChart.lineChart.invalidate()
                binding.dirawatTvline.paintFlags = binding.dirawatTvline.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateDirawat = 1
            }else{
                dataSetDirawat.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.dirawatTvline.paintFlags = binding.dirawatTvline.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateDirawat = 0
            }
        }
        var stateMeninggal = 0
        binding.linChartmeninggal.setOnClickListener {
            if (stateMeninggal==0){
                dataSetMeninggal.isVisible = false
                binding.relLayLineChart.lineChart.invalidate()
                binding.meninggalTvlinechart.paintFlags = binding.meninggalTvlinechart.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateMeninggal = 1
            }else{
                dataSetMeninggal.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.meninggalTvlinechart.paintFlags = binding.meninggalTvlinechart.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateMeninggal = 0
            }
        }

    }
    private fun dataProces(listData: List<ListData>){
        val sembuhEntries = ArrayList<Entry>()
        val meninggalEntries = ArrayList<Entry>()
        val dirawatEntries = ArrayList<Entry>()
        val label = ArrayList<String>()
        for (i in 0..5){
            sembuhEntries.add(Entry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
            meninggalEntries.add(Entry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
            dirawatEntries.add(Entry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            label.add(listData.get(i).key)
        }
        setupLineChart(sembuhEntries,dirawatEntries,meninggalEntries,label)
    }

}