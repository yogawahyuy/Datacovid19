package com.systudio.datacovid19.view.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
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
import com.systudio.datacovid19.databinding.FragmentStackBarBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.utils.marker.StackBarChartMarker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.custom_legend_filter.*
import kotlinx.android.synthetic.main.layout_stackbar_chart.*
import kotlinx.android.synthetic.main.layout_stackbar_chart.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [StackBarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class StackBarFragment : Fragment() {

    private var _binding :  FragmentStackBarBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStackBarBinding.inflate(inflater,container,false)
        initVm()
        return binding.root
    }

    private fun initVm(){
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(requireActivity()) {
            if (it != null) {
                setupStackedBarChart(it)
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupStackedBarChart(listData: List<ListData>){
        val legend = binding.stackbarchart.legend
        legend.isEnabled = false

        var totalSembuh = 0
        var totalMeninggal = 0
        var totalDirawat = 0
        var totalKasus = 0
        val dataStackBar = ArrayList<BarEntry>()
        val sembuh = ArrayList<BarEntry>()
        for (i in 0..5 ){
            sembuh.add(BarEntry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
            val floatArray = floatArrayOf(listData.get(i).jumlah_sembuh.toFloat(),listData.get(i).jumlah_dirawat.toFloat(),listData.get(i).jumlah_meninggal.toFloat())
            //dataStackBar.add(BarEntry(i.toFloat(),floatArray))
            totalSembuh += listData.get(i).jumlah_sembuh
        }
        val meninggal = ArrayList<BarEntry>()
        for (i in 0..5 ){
            meninggal.add(BarEntry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
            totalMeninggal += listData.get(i).jumlah_meninggal
        }
        val dirawat = ArrayList<BarEntry>()
        for (i in 0..5 ){
            dirawat.add(BarEntry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            totalDirawat += listData.get(i).jumlah_dirawat
        }
        //add total on top chart
        totalKasus += totalSembuh + totalDirawat + totalMeninggal
        binding.tvStackTotaldata.visibility = View.GONE
        val isiTv = ""+listData.get(0).jumlah_kasis + ", "+ listData.get(1).jumlah_kasis+" "+ listData.get(2).jumlah_kasis +
                ", "+ listData.get(3).jumlah_kasis + ", "+ listData.get(4).jumlah_kasis +", "+listData.get(5).jumlah_kasis
        binding.tvStackTotalperprovinsi.text = isiTv
        val sembuhBarDataSet = BarDataSet(sembuh,"Sembuh")
        sembuhBarDataSet.color = Color.GREEN
        val meninggalBarDataSet = BarDataSet(meninggal,"Meninggal")
        meninggalBarDataSet.color = Color.RED
        val dirawatBarDataSet = BarDataSet(dirawat,"dirawat")
        dirawatBarDataSet.color = Color.BLUE
        dirawatBarDataSet.setDrawValues(false)

        //val dataStackDataSet = BarDataSet(dataStackBar,"")
        //val colors = arrayOf(Color.GREEN,Color.BLUE,Color.RED)
        //dataStackDataSet.colors = colors.toMutableList()
        binding.stackbarchart.description.isEnabled = false
        binding.stackbarchart.xAxis.setDrawGridLines(false)
        binding.stackbarchart.axisLeft.setDrawGridLines(false)
        binding.stackbarchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.stackbarchart.axisRight.isEnabled = false
        binding.stackbarchart.axisLeft.axisMinimum = 0f
        binding.stackbarchart.setTouchEnabled(true)
        binding.stackbarchart.isDragEnabled = true
        binding.stackbarchart.setScaleEnabled(true)
        binding.stackbarchart.data = BarData(sembuhBarDataSet,dirawatBarDataSet,meninggalBarDataSet)
        binding.stackbarchart.animateXY(200,500)
        binding.stackbarchart.axisLeft.axisMaximum = binding.stackbarchart.data.yMax + 0.25f
        binding.stackbarchart.axisLeft.axisMinimum = binding.stackbarchart.data.yMin - 0.25f
        binding.stackbarchart.setDrawValueAboveBar(true)
        binding.stackbarchart.setFitBars(true)


        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        binding.stackbarchart.xAxis?.valueFormatter = IndexAxisValueFormatter(label)
        binding.stackbarchart.xAxis.isGranularityEnabled = true
        binding.stackbarchart.xAxis.granularity = 2f
        val marker = StackBarChartMarker(requireContext(),R.layout.custom_marker_view,label)
        binding.stackbarchart.marker = marker

        // legend filter
        var state = 0
        binding.linSembuhStack.setOnClickListener {
            if (state==0) {
                sembuhBarDataSet.isVisible = false
                binding.stackbarchart.invalidate()
                binding.sembuhTvStack.paintFlags = binding.sembuhTvStack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                state = 1
            }else {
                sembuhBarDataSet.isVisible = true
                binding.stackbarchart.invalidate()
                binding.sembuhTvStack.paintFlags = binding.sembuhTvStack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                state = 0
            }
        }
        var stateDirawat = 0
        binding.linDirawatStack.setOnClickListener {
            if (stateDirawat==0){
                dirawatBarDataSet.isVisible = false
                binding.stackbarchart.invalidate()
                binding.dirawatTvStack.paintFlags = binding.dirawatTvStack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateDirawat = 1
            }else{
                dirawatBarDataSet.isVisible = true
                binding.stackbarchart.invalidate()
                binding.dirawatTvStack.paintFlags = binding.dirawatTvStack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateDirawat = 0
            }
        }
        var stateMeninggal = 0
        binding.linMeninggalStack.setOnClickListener {
            if (stateMeninggal==0){
                meninggalBarDataSet.isVisible = false
                binding.stackbarchart.invalidate()
                binding.meninggalTvstack.paintFlags = binding.meninggalTvstack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateMeninggal = 1
            }else{
                meninggalBarDataSet.isVisible = true
                binding.stackbarchart.invalidate()
                binding.meninggalTvstack.paintFlags = binding.meninggalTvstack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateMeninggal = 0
            }
        }
    }



}