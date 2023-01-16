package com.systudio.datacovid19.view.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
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
        viewModel.fetchLiveData().observe(requireActivity()) {
            if (it != null) {
                setupPieChart(it)
            }
        }
        viewModel.fetchAllData()
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
        binding.piechart.legend.isEnabled = false
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