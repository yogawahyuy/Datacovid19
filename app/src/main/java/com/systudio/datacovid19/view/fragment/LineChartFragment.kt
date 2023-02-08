package com.systudio.datacovid19.view.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentLineChartBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.utils.marker.LineChartMarker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.custom_legend_filter.*
import kotlinx.android.synthetic.main.fragment_line_chart.view.*
import kotlinx.android.synthetic.main.layout_line_chart.*
import kotlinx.android.synthetic.main.layout_line_chart.view.*
import kotlinx.android.synthetic.main.layout_line_chart.view.lineChart
import kotlinx.android.synthetic.main.layout_stackbar_chart.*


/**
 * A simple [Fragment] subclass.
 * Use the [LineChartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LineChartFragment : Fragment(), OnChartValueSelectedListener {

    private var _binding : FragmentLineChartBinding? = null
    private val binding get() = _binding!!
    private lateinit var listData: List<ListData>
    private val removeIndex = arrayListOf<Int>()
    private val viewModel: MainViewModel by hiltNavGraphViewModels(R.id.main_navigation)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLineChartBinding.inflate(layoutInflater,container,false)
        //initVm()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVm()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initVm(){
        viewModel.fetchLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                listData = it
                dataProces()
                //dataProcess()
            }else{
                binding.cardviewLinechart.visibility = View.GONE
                binding.nointernet.relNointernet.visibility = View.VISIBLE
            }
        }
        //viewModel.fetchAllData()
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
        val markerView = LineChartMarker(requireContext(),R.layout.custom_marker_view,label)

        val lineChart = binding.lineChart
        lineChart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            animateXY(200,500)
            axisRight.isEnabled = false
            data = LineData(dataSetSembuh,dataSetDirawat,dataSetMeninggal)
            marker = markerView
        }
        val xAxis = binding.lineChart.xAxis
        xAxis.apply {
            granularity = 2f
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            textSize = 11f
            valueFormatter = IndexAxisValueFormatter(label)
        }
        val leftAxis = binding.lineChart.axisLeft
        leftAxis.apply {
            leftAxis.axisMinimum = 0f
            setDrawGridLines(false)
        }
        setupFilterBtn(dataSetSembuh,dataSetDirawat,dataSetMeninggal)
        // setupTooltip
        binding.lineChart.setOnChartValueSelectedListener(this)
        if (removeIndex.contains(1) && removeIndex.contains(2) && removeIndex.contains(3)){
            binding.relLayLineChart.tv_nodata.visibility = View.VISIBLE
            binding.relLayLineChart.lineChart.visibility = View.GONE
        }
    }

    private fun setupFilterBtn(dataSetSembuh: LineDataSet,dataSetDirawat: LineDataSet, dataSetMeninggal: LineDataSet){
        var state = 0
        binding.linChartsembuh.setOnClickListener {
            if (!removeIndex.contains(1)) {
                dataSetSembuh.isVisible = false
                //binding.relLayLineChart.lineChart.data.removeDataSet(0)
                binding.relLayLineChart.lineChart.invalidate()
                binding.sembuhTvline.paintFlags = binding.sembuhTvline.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                removeIndex.add(1)
                state = 1
            }else {
                dataSetSembuh.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.sembuhTvline.paintFlags = binding.sembuhTvline.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                removeIndex.remove(1)
                state = 0
            }
            if (removeIndex.contains(1) && removeIndex.contains(2) && removeIndex.contains(3)){
                binding.relLayLineChart.tv_nodata.visibility = View.VISIBLE
                binding.relLayLineChart.lineChart.visibility = View.GONE
            }else{
                binding.relLayLineChart.tv_nodata.visibility = View.GONE
                binding.relLayLineChart.lineChart.visibility = View.VISIBLE
            }
            //dataProces()
        }
        var stateDirawat = 0
        binding.linChartdirawat.setOnClickListener {
            if (!removeIndex.contains(2)){
                dataSetDirawat.isVisible = false
                //binding.relLayLineChart.lineChart.data.removeDataSet(1)
                binding.relLayLineChart.lineChart.invalidate()
                binding.dirawatTvline.paintFlags = binding.dirawatTvline.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                removeIndex.add(2)
                stateDirawat = 1
            }else{
                dataSetDirawat.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.dirawatTvline.paintFlags = binding.dirawatTvline.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                removeIndex.remove(2)
                stateDirawat = 0
            }
            if (removeIndex.contains(1) && removeIndex.contains(2) && removeIndex.contains(3)){
                binding.relLayLineChart.tv_nodata.visibility = View.VISIBLE
                binding.relLayLineChart.lineChart.visibility = View.GONE
            }else{
                binding.relLayLineChart.tv_nodata.visibility = View.GONE
                binding.relLayLineChart.lineChart.visibility = View.VISIBLE
            }
            //dataProces()
        }
        var stateMeninggal = 0
        binding.linChartmeninggal.setOnClickListener {
            if (!removeIndex.contains(3)){
                dataSetMeninggal.isVisible = false
                //binding.relLayLineChart.lineChart.data.removeDataSet(2)
                binding.relLayLineChart.lineChart.invalidate()
                binding.meninggalTvlinechart.paintFlags = binding.meninggalTvlinechart.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                removeIndex.add(3)
                stateMeninggal = 1
            }else{
                dataSetMeninggal.isVisible = true
                binding.relLayLineChart.lineChart.invalidate()
                binding.meninggalTvlinechart.paintFlags = binding.meninggalTvlinechart.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                removeIndex.remove(3)
                stateMeninggal = 0
            }
            if (removeIndex.contains(1) && removeIndex.contains(2) && removeIndex.contains(3)){
                binding.relLayLineChart.tv_nodata.visibility = View.VISIBLE
                binding.relLayLineChart.lineChart.visibility = View.GONE
            }else{
                binding.relLayLineChart.tv_nodata.visibility = View.GONE
                binding.relLayLineChart.lineChart.visibility = View.VISIBLE
            }
            //dataProces()
        }


    }

    private fun dataProces(){
        removeIndex.add(0)
        val sembuhEntries = ArrayList<Entry>()
        val meninggalEntries = ArrayList<Entry>()
        val dirawatEntries = ArrayList<Entry>()
        val label = ArrayList<String>()

        val conditionList = ArrayList<ArrayList<Entry>>()
        val filterCondition = ArrayList<ArrayList<Entry>>()

        for (i in 0..5) {
            sembuhEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_sembuh.toFloat()))
            meninggalEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_meninggal.toFloat()))
            dirawatEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_dirawat.toFloat()))
            label.add(listData.get(i).key)
        }

//        if (removeIndex.contains(0)) {
//
//        }else if (removeIndex.contains(1)){
//            for (i in 0..5) {
//                //sembuhEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_sembuh.toFloat()))
//                meninggalEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_meninggal.toFloat()))
//                dirawatEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_dirawat.toFloat()))
//            }
//        } else if (removeIndex.contains(2)){
//            for (i in 0..5) {
//                sembuhEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_sembuh.toFloat()))
//                //meninggalEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_meninggal.toFloat()))
//                dirawatEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_dirawat.toFloat()))
//            }
//        } else if (removeIndex.contains(3)){
//            for (i in 0..5) {
//                //sembuhEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_sembuh.toFloat()))
//                meninggalEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_meninggal.toFloat()))
//                dirawatEntries.add(Entry(i.toFloat(), listData.get(i).jumlah_dirawat.toFloat()))
//
//            }
//        }

        conditionList.add(sembuhEntries)
        conditionList.add(dirawatEntries)
        conditionList.add(meninggalEntries)

//        if (removeIndex.size>0){
//            if (removeIndex.size != conditionList.size){
//                for (index in removeIndex){
//                    filterCondition.add(conditionList[index])
//                }
//                for (condition in filterCondition){
//                    conditionList.remove(condition)
//                }
//            }
//        }

        //if ()

        setupLineChart(sembuhEntries,dirawatEntries,meninggalEntries,label)
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val highlight = IntArray(binding.lineChart.data.dataSets.size)
        for (i in 0 until binding.lineChart.data.dataSets.size){
            val iDataSet = binding.lineChart.data.dataSets.get(i)
            //for (j in 0 until iDataSet.)
        }
    }

    override fun onNothingSelected() {
        TODO("Not yet implemented")
    }

//    private fun setupValueSelectedListener() : OnChartValueSelectedListener{
//
//    }

    private fun setupLineChart(dataset: LineData, label: List<String>){
        val markerView = LineChartMarker(requireContext(),R.layout.custom_marker_view,label)
        //val dataset = LineDataSet(entries,"")
        val lineChart = binding.lineChart
        lineChart.apply {
            legend.isEnabled = false
            description.isEnabled = false
            animateXY(200,500)
            axisRight.isEnabled = false
            data = dataset
            marker = markerView
        }
        val xAxis = binding.lineChart.xAxis
        xAxis.apply {
            granularity = 2f
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            textSize = 11f
            valueFormatter = IndexAxisValueFormatter(label)
        }
        val leftAxis = binding.lineChart.axisLeft
        leftAxis.apply {
            leftAxis.axisMinimum = 0f
            setDrawGridLines(false)
        }
    }
    private fun dataProcess(){
        val entries = ArrayList<Entry>()
        val cityList = ArrayList<String>()
        val deaths = ArrayList<Float>()
        val treated = ArrayList<Float>()
        val recovered = ArrayList<Float>()

        val conditionList = ArrayList<ArrayList<Float>>()
        val filterCondition = ArrayList<ArrayList<Float>>()

        for (i in 0..5){
            cityList.add(listData[i].key)
            recovered.add(listData[i].jumlah_sembuh.toFloat())
            treated.add(listData[i].jumlah_dirawat.toFloat())
            deaths.add(listData[i].jumlah_meninggal.toFloat())
        }

        conditionList.add(recovered)
        conditionList.add(treated)
        conditionList.add(deaths)

        if (removeIndex.size > 0){
            if (removeIndex.size != conditionList.size){
                for (index in removeIndex){
                    filterCondition.add(conditionList[index])
                }
                for (condition in filterCondition){
                    conditionList.remove(condition)
                }
            }
        }
        val dataset = ArrayList<ILineDataSet>()
        if (conditionList.size != removeIndex.size){
            for (x in 0 until conditionList.size){
                val values = conditionList[x][0]
                entries.add(Entry(x.toFloat(),values))
                val lineData = LineDataSet(entries,"")
                dataset.add(lineData)
            }
            val data = LineData(dataset)
            setupLineChart(data,cityList)
        }else{
            binding.relLayLineChart.lineChart.clear()
            binding.relLayLineChart.lineChart.setNoDataTextColor(Color.BLACK)
            binding.relLayLineChart.lineChart.invalidate()
        }
    }


}