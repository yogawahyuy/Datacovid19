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
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.MPPointF
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentBarchartBinding
import com.systudio.datacovid19.databinding.FragmentPieChartBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.custom_totaldata_chart.*
import kotlinx.android.synthetic.main.layout_pie_chart.view.*


/**
 * A simple [Fragment] subclass.
 * Use the [PieChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class PieChartFragment : Fragment() {

    private var _binding : FragmentPieChartBinding? = null
    private val binding get() = _binding!!
    private val removeIndex = ArrayList<Int>()
    lateinit var listData: List<ListData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPieChartBinding.inflate(inflater,container,false)
        initVm()
        return binding.root
    }
    private fun initVm(){
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                //setupPieChart(it)
                listData = it
                dataProces()
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupPieCharts(entries: List<PieEntry>,color: ArrayList<Int>){
        val dataSet = PieDataSet(entries,"")
        dataSet.apply {
            setDrawIcons(false)
            sliceSpace = 6f
            iconsOffset = MPPointF(10f,70f)
            selectionShift = 5f
            colors = color
        }
        val pieChart = binding.piechart
        pieChart.apply {
            description.isEnabled = false
            setExtraOffsets(5f,10f,5f,5f)
            dragDecelerationFrictionCoef = 0.95f
            centerText = "Kasus"
            setCenterTextColor(Color.BLACK)
            setDrawCenterText(true)
            holeRadius = 58f
            transparentCircleRadius = 61f
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            animateY(1000,Easing.EaseInOutQuad)
            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(0f)
            data = PieData(dataSet)
            highlightValue(null)
            setTransparentCircleAlpha(110)
            minAngleForSlices = 20f
            setTransparentCircleColor(Color.WHITE)
            invalidate()
            setupFilterBtn()
        }

        val legend = binding.piechart.legend
        legend.apply {
            isEnabled = false
            isWordWrapEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            form = Legend.LegendForm.CIRCLE
            formSize = 20f
            formToTextSpace = 0f
            setDrawInside(false)
            orientation = Legend.LegendOrientation.HORIZONTAL
        }

    }

    private fun dataProces(){
        val pieEntry = ArrayList<PieEntry>()
        val recovered = ArrayList<Float>()
        val death = ArrayList<Float>()
        val treated = ArrayList<Float>()

        val conditionalList = ArrayList<ArrayList<Float>>()
        val filterCondition = ArrayList<ArrayList<Float>>()
        val color = ArrayList<Int>()
        val filterColor = ArrayList<Int>()

        var totalTreated = 0f
        var totalRecovered = 0f
        var totalDeath = 0f

        for (i in 0..5 ){
            totalTreated += listData.get(i).jumlah_dirawat.toFloat()
            totalRecovered += listData.get(i).jumlah_sembuh.toFloat()
            totalDeath += listData.get(i).jumlah_meninggal.toFloat()
        }

        recovered.add(totalRecovered)
        death.add(totalDeath)
        treated.add(totalTreated)

        conditionalList.add(recovered)
        conditionalList.add(death)
        conditionalList.add(treated)

        color.add(Color.GREEN)
        color.add(Color.RED)
        color.add(Color.BLUE)

        if (removeIndex.size > 0){
            if (removeIndex.size != conditionalList.size){
                for (index in removeIndex){
                    filterCondition.add(conditionalList[index])
                    filterColor.add(color[index])
                }
                for (filterIndex in filterCondition){
                    conditionalList.remove(filterIndex)
                }
                for (colors in filterColor){
                    color.remove(colors)
                }
            }
        }
        if (conditionalList.size != removeIndex.size){
            for (x in 0 until conditionalList.size){
                val values = conditionalList[x][0]
                pieEntry.add(PieEntry(values))
            }
            setupPieCharts(pieEntry,color)
        } else {
            binding.piechart.clear()
            binding.piechart.setNoDataTextColor(Color.BLACK)
            binding.piechart.invalidate()
        }

    }

    private fun setupFilterBtn(){
        binding.linPiechartdki.setOnClickListener {
            if (!removeIndex.contains(0)){
                removeIndex.add(0)
                binding.dkiTvPie.paintFlags = binding.dkiTvPie.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
            }else{
                binding.dkiTvPie.paintFlags = binding.dkiTvPie.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                removeIndex.remove(0)
            }
            dataProces()
        }
        binding.linChartjabar.setOnClickListener {
            if (!removeIndex.contains(1)){
                removeIndex.add(1)
                binding.jabarTvline.paintFlags = binding.jabarTvline.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
            } else{
                removeIndex.remove(1)
                binding.jabarTvline.paintFlags = binding.jabarTvline.paintFlags and (Paint.ANTI_ALIAS_FLAG)
            }
            dataProces()
        }
        binding.linChartjatengl.setOnClickListener {
            if (!removeIndex.contains(2)){
                removeIndex.add(2)
                binding.jatengTvlinechart.paintFlags = binding.jatengTvlinechart.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                binding.jatengTvlinechart.paintFlags = binding.jatengTvlinechart.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                removeIndex.remove(2)
            }
            dataProces()
        }
    }
    private fun setupPieChart(listData: List<ListData>){
        binding.piechart.description.isEnabled = false
        binding.piechart.setExtraOffsets(5f,10f,5f,5f)
        binding.piechart.dragDecelerationFrictionCoef=0.95f
        binding.piechart.centerText = "Jumlah Sembuh"
        binding.piechart.setCenterTextColor(Color.BLACK)
        binding.piechart.setDrawCenterText(true)

        binding.piechart.isDrawHoleEnabled = true
        binding.piechart.setHoleColor(Color.WHITE)

        binding.piechart.setTransparentCircleAlpha(110)
        binding.piechart.setTransparentCircleColor(Color.WHITE)

        binding.piechart.holeRadius =58f
        binding.piechart.transparentCircleRadius = 61f

        binding.piechart.rotationAngle = 0f

        binding.piechart.isRotationEnabled = true
        binding.piechart.isHighlightPerTapEnabled = true

        binding.piechart.animateY(1000, Easing.EaseInOutQuad)
        binding.piechart.setEntryLabelColor(Color.WHITE)
        binding.piechart.setEntryLabelTextSize(0f)

        binding.piechart.highlightValue(null)

        var totalSembuh = 0
        val sembuh = ArrayList<PieEntry>()
        for (i in 0..5 ){
            sembuh.add(PieEntry(listData.get(i).jumlah_sembuh.toFloat(),listData.get(i).key))
            totalSembuh += listData.get(i).jumlah_sembuh
        }
        //binding. = "Total Sembuh : "+totalSembuh
        val sembuhPieDataSet = PieDataSet(sembuh,"")
        sembuhPieDataSet.setDrawIcons(false)
        sembuhPieDataSet.sliceSpace = 6f
        sembuhPieDataSet.iconsOffset = MPPointF(10f,70f)
        sembuhPieDataSet.selectionShift = 5f

        val color = ArrayList<Int>()
        color.add(Color.RED)
        color.add(Color.BLUE)
        color.add(Color.YELLOW)
        color.add(Color.GREEN)
        color.add(Color.GRAY)
        color.add(Color.CYAN)
        sembuhPieDataSet.colors = color

        val legend = binding.piechart.legend
        binding.piechart.legend.isWordWrapEnabled = true
        binding.piechart.legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.formSize = 20f
        legend.formToTextSpace = 0f
        legend.form = Legend.LegendForm.CIRCLE
        legend.textSize = 10f
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        val data = PieData(sembuhPieDataSet)
        binding.piechart.data = data

    }


}