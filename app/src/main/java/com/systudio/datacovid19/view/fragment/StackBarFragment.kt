package com.systudio.datacovid19.view.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setMargins
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentStackBarBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.utils.MyAxisValueFormatter
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
class StackBarFragment : Fragment(), OnChartValueSelectedListener {

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
                //setupStackedBarChart(it)
                //setupNewStackedBarChart(it)
                dataProces(it)
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupStackBarChart(entries: List<BarEntry>, label: List<String>){
        val dataset = BarDataSet(entries,"")
        dataset.colors = getColor()
        dataset.setDrawValues(false)
        val stackChart = binding.stackbarchart
        stackChart.apply {
            setPinchZoom(false)
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            isHighlightFullBarEnabled = false
            animateXY(200,500)
            legend.isEnabled = false
            description.isEnabled = false
            data = BarData(dataset)
        }
        val xAxis = binding.stackbarchart.xAxis
        xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 2f
            valueFormatter = IndexAxisValueFormatter(label)

        }
        val leftAxis = binding.stackbarchart.axisLeft
        leftAxis.apply {
            setDrawGridLines(false)
            axisMinimum = 0f
        }
        val rightAxis = binding.stackbarchart.axisRight
        rightAxis.apply {
            isEnabled = false
        }
    }
    private fun dataProces(listData: List<ListData>){
        val myTextView = arrayListOf<TextView>()
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(5,4,4,4)
        val barEntriesList = ArrayList<BarEntry>()
        val label = ArrayList<String>()
        for (i in 0..5){
            val floatArray = floatArrayOf(listData.get(i).jumlah_sembuh.toFloat(),listData.get(i).jumlah_dirawat.toFloat(),listData.get(i).jumlah_meninggal.toFloat())
            barEntriesList.add(BarEntry(i.toFloat(),floatArray))
            label.add(listData.get(i).key)
            myTextView.add(TextView(requireContext()))
        }
        for (i in myTextView.indices) {
            myTextView[i].text = listData.get(i).jumlah_kasis.toString()
            myTextView[i].textSize = 8f
            myTextView.get(i).layoutParams = param
            binding.linStackTotaldata.addView(myTextView[i])
            Log.d("stackbar dataproses", "dataProces: i"+i)
        }
        Log.d("stackbar dataproses", "dataProces: "+myTextView.size)
        setupStackBarChart(barEntriesList,label)
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
        val meninggal = ArrayList<BarEntry>()
        val dirawat = ArrayList<BarEntry>()
        for (i in 0..5 ){
            sembuh.add(BarEntry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
            meninggal.add(BarEntry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
            dirawat.add(BarEntry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            val floatArray = floatArrayOf(listData.get(i).jumlah_sembuh.toFloat(),listData.get(i).jumlah_dirawat.toFloat(),listData.get(i).jumlah_meninggal.toFloat())
            //dataStackBar.add(BarEntry(i.toFloat(),floatArray))
            //dataStackBar.add(BarEntry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
            //dataStackBar.add(BarEntry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            //dataStackBar.add(BarEntry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
            totalSembuh += listData.get(i).jumlah_sembuh
        }

        for (i in 0..5 ){
            //meninggal.add(BarEntry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
            totalMeninggal += listData.get(i).jumlah_meninggal
        }

        for (i in 0..5 ){
            //dirawat.add(BarEntry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            totalDirawat += listData.get(i).jumlah_dirawat
        }
        //add total on top chart
        totalKasus += totalSembuh + totalDirawat + totalMeninggal
        binding.tvStackTotaldata.visibility = View.GONE
        val isiTv = ""+listData.get(0).jumlah_kasis + ", "+ listData.get(1).jumlah_kasis+" "+ listData.get(2).jumlah_kasis +
                ", "+ listData.get(3).jumlah_kasis + ", "+ listData.get(4).jumlah_kasis +", "+listData.get(5).jumlah_kasis
        binding.tvStackTotalperprovinsi.text = isiTv
        val sembuhBarDataSet = BarDataSet(sembuh,"")
        sembuhBarDataSet.color = Color.GREEN
        val meninggalBarDataSet = BarDataSet(meninggal,"")
        meninggalBarDataSet.color = Color.RED
        val dirawatBarDataSet = BarDataSet(dirawat,"")
        dirawatBarDataSet.color = Color.BLUE
        dirawatBarDataSet.setDrawValues(false)

//        val dataStackDataSet = BarDataSet(dataStackBar,"")
//        val colors = arrayOf(Color.GREEN,Color.BLUE,Color.RED)
//        dataStackDataSet.colors = colors.toMutableList()
        val dataset = ArrayList<IBarDataSet>()
        dataset.add(sembuhBarDataSet)
        dataset.add(dirawatBarDataSet)
        dataset.add(meninggalBarDataSet)

        binding.stackbarchart.description.isEnabled = false
        binding.stackbarchart.xAxis.setDrawGridLines(false)
        binding.stackbarchart.axisLeft.setDrawGridLines(false)
        binding.stackbarchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.stackbarchart.axisRight.isEnabled = false
        binding.stackbarchart.axisLeft.axisMinimum = 0f
        binding.stackbarchart.setTouchEnabled(true)
        binding.stackbarchart.isDragEnabled = true
        binding.stackbarchart.setScaleEnabled(true)
        binding.stackbarchart.data = BarData(dataset)
        //binding.stackbarchart.data = BarData(dataStackDataSet)
        binding.stackbarchart.animateXY(200,500)
        binding.stackbarchart.setDrawValueAboveBar(true)
        binding.stackbarchart.setFitBars(true)
        binding.stackbarchart.data.getGroupWidth(3f,2f)
        binding.stackbarchart.axisLeft.spaceMin = 1f

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
                Log.d("stackbarfragment", "setupNewStackedBarChart: "+binding.stackbarchart.data.dataSets.size)
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

    private fun setupNewStackedBarChart(listData: List<ListData>){
        binding.stackbarchart.setMaxVisibleValueCount(40)
        binding.stackbarchart.legend.isEnabled = false
        binding.stackbarchart.description.isEnabled = false
        binding.stackbarchart.setPinchZoom(false)
        binding.stackbarchart.setDrawGridBackground(false)
        binding.stackbarchart.setDrawBarShadow(false)
        binding.stackbarchart.setDrawValueAboveBar(false)
        binding.stackbarchart.isHighlightFullBarEnabled = false
        binding.stackbarchart.xAxis.setDrawGridLines(false)
        binding.stackbarchart.axisLeft.setDrawGridLines(false)
        binding.stackbarchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.stackbarchart.axisRight.isEnabled = false
        binding.stackbarchart.axisLeft.axisMinimum = 0f
        binding.stackbarchart.animateXY(200,500)

        val leftAxis = binding.stackbarchart.axisLeft
        leftAxis.valueFormatter = MyAxisValueFormatter()
        leftAxis.axisMinimum = 0f
        binding.stackbarchart.axisRight.isEnabled = false
        //total data on top
        var totalSembuh = 0
        var totalMeninggal = 0
        var totalDirawat = 0
        var totalKasus = 0

        //setting data
        val data = ArrayList<BarEntry>()
        for (i in 0..5){
            val cumulativeData = floatArrayOf(listData.get(i).jumlah_sembuh.toFloat(),listData.get(i).jumlah_dirawat.toFloat(),listData.get(i).jumlah_meninggal.toFloat())
            data.add(BarEntry(i.toFloat(),cumulativeData))
            totalSembuh += listData.get(i).jumlah_sembuh
            totalDirawat += listData.get(i).jumlah_dirawat
            totalMeninggal += listData.get(i).jumlah_meninggal
        }

        totalKasus += totalSembuh + totalDirawat + totalMeninggal
        binding.tvStackTotaldata.visibility = View.GONE
        val isiTv = ""+listData.get(0).jumlah_kasis + ", "+ listData.get(1).jumlah_kasis+" "+ listData.get(2).jumlah_kasis +
                ", "+ listData.get(3).jumlah_kasis + ", "+ listData.get(4).jumlah_kasis +", "+listData.get(5).jumlah_kasis
        binding.tvStackTotalperprovinsi.text = isiTv

        val bardata : BarDataSet
        if (binding.stackbarchart.data != null && binding.stackbarchart.data.dataSetCount > 0){
            bardata = binding.stackbarchart.data.getDataSetByIndex(0) as BarDataSet
            bardata.values = data
            binding.stackbarchart.data.notifyDataChanged()
            binding.stackbarchart.notifyDataSetChanged()
        }else{
            bardata = BarDataSet(data,"")
            bardata.setDrawValues(false)
            bardata.setDrawIcons(false)
            bardata.colors = getColor()

            val dataset = ArrayList<IBarDataSet>()
            dataset.add(bardata)

            val label = ArrayList<String>()
            for (i in 0..5){
                label.add(listData.get(i).key)
            }
            val data1 = BarData(dataset)
            data1.setValueFormatter(MyAxisValueFormatter())
            val formater = IndexAxisValueFormatter(label)
            binding.stackbarchart.xAxis.valueFormatter = formater
            binding.stackbarchart.xAxis.granularity = 2f
            binding.stackbarchart.data = data1
            val marker = StackBarChartMarker(requireContext(),R.layout.custom_marker_view,label)
            binding.stackbarchart.marker = marker
            Log.d("stackbarfragment", "setupNewStackedBarChart: "+bardata.values.get(0).yVals.get(0).toString())
            Log.d("stackbarfragment", "setupNewStackedBarChart: "+binding.stackbarchart.data.dataSets.get(0))
        }

        binding.stackbarchart.setFitBars(true)
        binding.stackbarchart.invalidate()
        // legend filter
        var state = 0
        binding.linSembuhStack.setOnClickListener {
            if (state==0) {
                //sembuhBarDataSet.isVisible = false
                Log.d("stackbarfrag onclik", "setupNewStackedBarChart: " +binding.stackbarchart.barData.dataSets.get(0).getIndexInEntries(1).toString())
                binding.stackbarchart.data.dataSets.get(0).isVisible = false
                binding.stackbarchart.invalidate()
                binding.sembuhTvStack.paintFlags = binding.sembuhTvStack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                state = 1
            }else {
                //sembuhBarDataSet.isVisible = true
                binding.stackbarchart.data.dataSets.get(0).isVisible = true
                binding.stackbarchart.invalidate()
                binding.sembuhTvStack.paintFlags = binding.sembuhTvStack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                state = 0
            }
        }
        var stateDirawat = 0
        binding.linDirawatStack.setOnClickListener {
            if (stateDirawat==0){
                //dirawatBarDataSet.isVisible = false
                binding.stackbarchart.invalidate()
                binding.dirawatTvStack.paintFlags = binding.dirawatTvStack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateDirawat = 1
            }else{
                //dirawatBarDataSet.isVisible = true
                binding.stackbarchart.invalidate()
                binding.dirawatTvStack.paintFlags = binding.dirawatTvStack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateDirawat = 0
            }
        }
        var stateMeninggal = 0
        binding.linMeninggalStack.setOnClickListener {
            if (stateMeninggal==0){
                //meninggalBarDataSet.isVisible = false
                binding.stackbarchart.invalidate()
                binding.meninggalTvstack.paintFlags = binding.meninggalTvstack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateMeninggal = 1
            }else{
                //meninggalBarDataSet.isVisible = true
                binding.stackbarchart.invalidate()
                binding.meninggalTvstack.paintFlags = binding.meninggalTvstack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateMeninggal = 0
            }
        }
    }

    private fun getColor() : MutableList<Int>{
        val mutableListInt : MutableList<Int> = mutableListOf()
        mutableListInt.add(Color.GREEN)
        mutableListInt.add(Color.BLUE)
        mutableListInt.add(Color.RED)
        return mutableListInt
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val entry = e
        if (entry?.y !=null){
            Log.d("stackbarfrag", "onValueSelected: "+entry.data)
        }
    }

    override fun onNothingSelected() {
        TODO("Not yet implemented")
    }


}