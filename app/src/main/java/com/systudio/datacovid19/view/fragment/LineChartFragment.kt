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
                setupLineChart(it)
            }
        }
        viewModel.fetchAllData()
    }
    private fun setupLineChart(listData: List<ListData>){
        val sembuh = ArrayList<Entry>()
        for (i in 0..5 ){
            Log.d("setuplinechart", "setupLineChart: "+listData.get(i).jumlah_sembuh)
            sembuh.add(Entry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
        }
        val meninggal = ArrayList<Entry>()
        for (i in 0..5 ){
            meninggal.add(Entry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
        }
        val dirawat = ArrayList<Entry>()
        for (i in 0..5 ){
            dirawat.add(Entry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
        }
        val sembuhLineDataset = LineDataSet(sembuh,"Sembuh")
        sembuhLineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        sembuhLineDataset.color = Color.GREEN
        sembuhLineDataset.circleRadius = 5f
        sembuhLineDataset.setCircleColor(Color.GREEN)

        val dirawatLineDataSet = LineDataSet(dirawat,"dirawat")
        dirawatLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dirawatLineDataSet.color = Color.BLUE
        dirawatLineDataSet.circleRadius = 5f
        dirawatLineDataSet.setCircleColor(Color.GRAY)

        val meninggalLineDataset = LineDataSet(meninggal,"Meninggal")
        meninggalLineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        meninggalLineDataset.color = Color.RED
        meninggalLineDataset.circleRadius = 5f
        meninggalLineDataset.setCircleColor(Color.RED)

        val legend = binding.lineChart.legend
        legend.isEnabled = false
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        binding.lineChart.description.isEnabled = false
        binding.lineChart.axisLeft.axisMinimum = 0f
        binding.lineChart.xAxis.granularity = 2f
        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.axisLeft.setDrawGridLines(false)
        binding.lineChart.data = LineData( sembuhLineDataset, dirawatLineDataSet, meninggalLineDataset)
        binding.lineChart.animateXY(100, 500)
        val rightAxis = binding.lineChart.axisRight
        rightAxis.isEnabled = false

        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        binding.lineChart.xAxis?.valueFormatter = IndexAxisValueFormatter(label)
        val marker = LineChartMarker(requireContext(),R.layout.custom_marker_view)
        binding.relLayLineChart.lineChart.marker = marker

        //legend filter
        var state = 0
        binding.linChartsembuh.setOnClickListener {
            if (state==0) {
                sembuhLineDataset.isVisible = false
                binding.relLayLineChart.lineChart.invalidate()
                binding.sembuhTvline.paintFlags = binding.sembuhTvline.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                state = 1
            }else {
                sembuhLineDataset.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.sembuhTvline.paintFlags = binding.sembuhTvline.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                state = 0
            }
        }
        var stateDirawat = 0
        binding.linChartdirawat.setOnClickListener {
            if (stateDirawat==0){
                dirawatLineDataSet.isVisible = false
                binding.relLayLineChart.lineChart.invalidate()
                binding.dirawatTvline.paintFlags = binding.dirawatTvline.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateDirawat = 1
            }else{
                dirawatLineDataSet.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.dirawatTvline.paintFlags = binding.dirawatTvline.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateDirawat = 0
            }
        }
        var stateMeninggal = 0
        binding.linChartmeninggal.setOnClickListener {
            if (stateMeninggal==0){
                meninggalLineDataset.isVisible = false
                binding.relLayLineChart.lineChart.invalidate()
                binding.meninggalTvlinechart.paintFlags = binding.meninggalTvlinechart.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateMeninggal = 1
            }else{
                meninggalLineDataset.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.meninggalTvlinechart.paintFlags = binding.meninggalTvlinechart.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateMeninggal = 0
            }
        }

    }
}