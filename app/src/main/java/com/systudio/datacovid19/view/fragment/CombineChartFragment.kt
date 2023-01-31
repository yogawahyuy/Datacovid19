package com.systudio.datacovid19.view.fragment

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentCombineChartBinding
import com.systudio.datacovid19.databinding.FragmentPieChartBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.utils.marker.BarChartMarkerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.layout_combine_chart.*
import kotlinx.android.synthetic.main.layout_combine_chart.view.*
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 * Use the [CombineChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CombineChartFragment : Fragment() {
    private var _binding : FragmentCombineChartBinding? = null
    private val binding get() = _binding!!
    lateinit var listData: List<ListData>
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
        viewModel.fetchLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                //setupCombinedChart(it)
                listData = it
                setupCombinedChart()

            }else{
                binding.combineFragmentToolbar.visibility = View.GONE
                binding.nointernet.relNointernet.visibility = View.VISIBLE
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupCombinedChart(){
        val combinedata = CombinedData()
        combinedata.setData(generateBarData())
        combinedata.setData(generateLineData())
        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        //val markerView = BarChartMarkerView(requireContext(),R.layout.custom_marker_view, label)
        val markerView : IMarker = BarChartMarkerView(requireContext(),R.layout.custom_marker_view,label)
        val combineChart = binding.combinedChart
        combineChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            isHighlightFullBarEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            isDragXEnabled = true
            legend.isEnabled = false
            data = combinedata
            marker = markerView
            invalidate()

        }

        val rightAxis = binding.combinedChart.axisRight
        rightAxis.apply {
            setDrawGridLines(false)
            isEnabled = false
        }

        val leftAxis = binding.combinedChart.axisLeft
        leftAxis.apply {
            setDrawGridLines(false)
            axisMinimum = 0f
        }

        val axis = binding.combinedChart.xAxis
        axis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 2f
            setDrawGridLines(false)
            axis.valueFormatter = IndexAxisValueFormatter(label)
            axisMinimum = combinedata.xMin - 0.5f
            axisMaximum = combinedata.xMax + 0.5f
        }

        setupTopValue()
    }

    private fun generateLineData() : LineData {
        var totalSembuh = 0
        var totalMeninggal = 0
        var totalDirawat = 0
        val linedata = LineData()
        val sembuh = ArrayList<Entry>()
        val meninggal = ArrayList<Entry>()
        val dirawat = ArrayList<Entry>()
        for (i in 0..5 ){
            sembuh.add(Entry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
            meninggal.add(Entry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
            dirawat.add(Entry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            totalSembuh += listData.get(i).jumlah_sembuh
            totalMeninggal += listData.get(i).jumlah_meninggal
            totalDirawat += listData.get(i).jumlah_dirawat
        }
        val sembuhLineDataset = LineDataSet(sembuh,"Sembuh")
        sembuhLineDataset.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.GREEN
            circleRadius = 5f
            setCircleColor(Color.GREEN)
        }
        val dirawatLineDataSet = LineDataSet(dirawat,"dirawat")
        dirawatLineDataSet.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.BLUE
            circleRadius = 5f
            setCircleColor(Color.GRAY)
        }
        val meninggalLineDataset = LineDataSet(meninggal,"Meninggal")
        meninggalLineDataset.apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = Color.RED
            circleRadius = 5f
            setCircleColor(Color.RED)
        }
        linedata.addDataSet(meninggalLineDataset)
        linedata.addDataSet(dirawatLineDataSet)
        linedata.addDataSet(sembuhLineDataset)
        //legend filter
        setupLineBtn(sembuhLineDataset,dirawatLineDataSet,meninggalLineDataset)

        return linedata
    }

    private fun generateBarData(): BarData {
        val barEntriesList = ArrayList<BarEntry>()
        for (i in 0..5) {
            barEntriesList.add(BarEntry(i.toFloat(), listData.get(i).jumlah_kasis.toFloat()))
        }
        val barDataset = BarDataSet(barEntriesList, "Jumlah Kasus")
        barDataset.color = Color.GRAY
        barDataset.setDrawValues(false)
        setupBarBtn(barDataset)
        return BarData(barDataset)
    }

    private fun setupTopValue(){
        val myTextView = arrayListOf<TextView>()
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(30,10,10,10)
        for (i in 0..5){
            val number = listData.get(i).jumlah_kasis
            myTextView.add(TextView(requireContext()))
            myTextView[i].text = formatNumber(number)
            myTextView[i].textSize = 8f
            myTextView[i].typeface = Typeface.DEFAULT
            myTextView[i].setTextColor(Color.BLACK)
            myTextView.get(i).layoutParams = param
            binding.linCombineTotaldata.addView(myTextView[i])
        }
    }

    private fun setupLineBtn(sembuhLineDataset: LineDataSet,dirawatLineDataSet: LineDataSet,meninggalLineDataset: LineDataSet){
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
                if (stateMeninggal == 0) {
                    meninggalLineDataset.isVisible = false
                    binding.combinedChart.invalidate()
                    binding.meninggalTvCombine.paintFlags =
                        binding.meninggalTvCombine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                    stateMeninggal = 1
                } else {
                    meninggalLineDataset.isVisible = true
                    binding.combinedChart.invalidate()
                    binding.meninggalTvCombine.paintFlags =
                        binding.meninggalTvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                    stateMeninggal = 0
                }
            }
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

    private fun setupBarBtn(barDataSet: BarDataSet){
        var stateKasus = 0
        binding.linCombineTotalkasus.setOnClickListener {
            if (stateKasus==0){
                barDataSet.isVisible = false
                binding.combinedChart.invalidate()
                binding.kasustvCombine.paintFlags = binding.kasustvCombine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateKasus = 1
            }else {
                barDataSet.isVisible = true
                binding.combinedChart.invalidate()
                binding.kasustvCombine.paintFlags = binding.kasustvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateKasus = 0
            }
        }
    }

    private fun formatNumber(number: Int): String {
        if (number >= 1000000){
            val formatter = DecimalFormat("#,###.#")
            return formatter.format(number / 1000.0) + "K"
        } else {
            val formatter = DecimalFormat("#,###.#")
            return formatter.format(number / 1000.0) +"K"
        }

    }


}