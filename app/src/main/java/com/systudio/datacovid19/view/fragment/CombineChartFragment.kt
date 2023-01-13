package com.systudio.datacovid19.view.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentCombineChartBinding
import com.systudio.datacovid19.databinding.FragmentPieChartBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.layout_combine_chart.*
import kotlinx.android.synthetic.main.layout_combine_chart.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [CombineChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CombineChartFragment : Fragment() {
    private var _binding : FragmentCombineChartBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCombineChartBinding.inflate(inflater,container,false)
        initVm()
        return binding.root
    }

    private fun initVm(){
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(requireActivity()) {
            if (it != null) {
                setupCombinedChart(it)
            }
        }
        viewModel.fetchAllData()
    }
    private fun setupCombinedChart(listData: List<ListData>){
        binding.combinedChart.description.isEnabled = false
        binding.combinedChart.setDrawGridBackground(false)
        binding.combinedChart.setDrawBarShadow(false)
        binding.combinedChart.isHighlightFullBarEnabled = false
        binding.combinedChart.setTouchEnabled(true)
        binding.combinedChart.setPinchZoom(true)
        binding.combinedChart.isDragXEnabled = true

        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }

        val legend = binding.combinedChart.legend
        legend.isEnabled = false
        legend.isWordWrapEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        val rightAxis = binding.combinedChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.axisMinimum = 0f
        rightAxis.isEnabled = false

        val leftAxis = binding.combinedChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 1f

        val axis = binding.combinedChart.xAxis
        axis.position = XAxis.XAxisPosition.BOTTOM
        axis.axisMinimum = 0f
        axis.granularity = 2f
        axis.setDrawGridLines(false)
        axis.valueFormatter = IndexAxisValueFormatter(label)

        val combinedata = CombinedData()

        combinedata.setData(generateBarData(listData))
        combinedata.setData(generateLineData(listData))

        binding.combinedChart.data = combinedata

    }

    private fun generateLineData(listData: List<ListData>) : LineData {
        var totalSembuh = 0
        var totalMeninggal = 0
        var totalDirawat = 0
        val linedata = LineData()
        val sembuh = ArrayList<Entry>()
        for (i in 0..5 ){
            sembuh.add(Entry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
            totalSembuh += listData.get(i).jumlah_sembuh
        }
        val meninggal = ArrayList<Entry>()
        for (i in 0..5 ){
            meninggal.add(Entry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
            totalMeninggal += listData.get(i).jumlah_meninggal
        }
        val dirawat = ArrayList<Entry>()
        for (i in 0..5 ){
            dirawat.add(Entry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            totalDirawat += listData.get(i).jumlah_dirawat
        }
        var totalKasus = 0
        totalKasus += totalSembuh + totalDirawat + totalMeninggal
        binding.tvCombineTotalperprovinsi.visibility=View.GONE
        val isiTv = ""+listData.get(0).jumlah_kasis + ", "+ listData.get(1).jumlah_kasis+" "+ listData.get(2).jumlah_kasis +
                ", "+ listData.get(3).jumlah_kasis + ", "+ listData.get(4).jumlah_kasis +", "+listData.get(5).jumlah_kasis
        binding.tvCombineTotaldata.text = isiTv
        //tv_combine_totaldata.text = "total sembuh : " + totalSembuh+ ", Total dirawat : " + totalDirawat +", Total Meninggal : "+totalMeninggal+", Total Kasus : "+totalKasus
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

        linedata.addDataSet(meninggalLineDataset)
        linedata.addDataSet(dirawatLineDataSet)
        linedata.addDataSet(sembuhLineDataset)
        //legend filter
        var sembuhState = 0
        binding.linCombineSembuh.setOnClickListener {
            if (sembuhState==0){
                sembuhLineDataset.isVisible = false
                binding.combinedChart.invalidate()
                binding.sembuhTvCombine.paintFlags = binding.sembuhTvCombine.paintFlags  or (Paint.STRIKE_THRU_TEXT_FLAG)
                sembuhState = 1
            }else{
                sembuhLineDataset.isVisible = true
                combinedChart.invalidate()
                binding.sembuhTvCombine.paintFlags = binding.sembuhTvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                sembuhState = 0
            }
            var stateDirawat = 0
            binding.linCombineDirawat.setOnClickListener {
                if (stateDirawat==0){
                    dirawatLineDataSet.isVisible = false
                    binding.combinedChart.invalidate()
                    binding.dirawatTvCombine.paintFlags = binding.dirawatTvCombine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                    stateDirawat = 1
                }else{
                    dirawatLineDataSet.isVisible = true
                    binding.combinedChart.invalidate()
                    binding.dirawatTvCombine.paintFlags = binding.dirawatTvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                    stateDirawat = 0
                }
            }
            var stateMeninggal = 0
            binding.linCombineMeninggal.setOnClickListener {
                if (stateMeninggal==0){
                    meninggalLineDataset.isVisible = false
                    binding.combinedChart.invalidate()
                    binding.meninggalTvCombine.paintFlags = binding.meninggalTvCombine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                    stateMeninggal = 1
                }else{
                    meninggalLineDataset.isVisible = true
                    binding.combinedChart.invalidate()
                    binding.meninggalTvCombine.paintFlags = binding.meninggalTvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                    stateMeninggal = 0
                }
            }
        }
        return linedata
    }

    private fun generateBarData(listData: List<ListData>): BarData {
        val barEntriesList = ArrayList<BarEntry>()

        for (i in 0..5) {
            barEntriesList.add(BarEntry(i.toFloat(), listData.get(i).jumlah_kasis.toFloat()))
        }
        val meninggal = ArrayList<BarEntry>()
        for (i in 0..5 ){
            meninggal.add(BarEntry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
        }
        val barDataset = BarDataSet(barEntriesList, "Jumlah Kasus")
        barDataset.color = Color.GRAY
        val meninggalDataSet = BarDataSet(meninggal,"")
        meninggalDataSet.color = Color.RED

        var stateKasus = 0
        binding.linCombineTotalkasus.setOnClickListener {
            if (stateKasus==0){
                barDataset.isVisible = false
                binding.combinedChart.invalidate()
                binding.kasustvCombine.paintFlags = binding.kasustvCombine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateKasus = 1
            }else {
                barDataset.isVisible = true
                binding.combinedChart.invalidate()
                binding.kasustvCombine.paintFlags = binding.kasustvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateKasus = 0
            }
        }

        return BarData(barDataset)
    }


}