package com.systudio.datacovid19.view.fragment

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.systudio.datacovid19.utils.marker.BarChartMarkerView
import com.systudio.datacovid19.utils.marker.StackBarChartMarker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.custom_alert_dialog.*
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import kotlinx.android.synthetic.main.custom_legend_filter.*
import kotlinx.android.synthetic.main.layout_stackbar_chart.*
import kotlinx.android.synthetic.main.layout_stackbar_chart.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass.
 * Use the [StackBarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class StackBarFragment : Fragment(){

    private var _binding :  FragmentStackBarBinding? = null
    private val binding get() = _binding!!
    lateinit var myTextView: ArrayList<TextView>
    lateinit var listData: List<ListData>
    private val removeIndex = arrayListOf<Int>()

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
        viewModel.fetchLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                listData = it
                dataProcess()
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupStackBarChart(entries: List<BarEntry>, colorList: List<Int>, label: List<String>){
        val markerView = StackBarChartMarker(requireContext(),R.layout.custom_marker_view,label)
        val dataset = BarDataSet(entries,"")
        dataset.colors = colorList
        dataset.setDrawValues(false)
        val stackChart = binding.stackbarchart
        stackChart.apply {
            animateY(1000)
            setDrawValueAboveBar(false)
            setDrawMarkers(true)
            isDoubleTapToZoomEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            data = BarData(dataset)
            marker = markerView
            invalidate()
            setupFilterBtn()

        }
        val xAxis = binding.stackbarchart.xAxis
        xAxis.apply {
            setDrawGridLines(false)
            setDrawLabels(true)
            setDrawAxisLine(true)
            textSize = 11f
            position = XAxis.XAxisPosition.BOTTOM
            isGranularityEnabled = true
            granularity = 1f
            setLabelCount(3, false)
            valueFormatter = IndexAxisValueFormatter(label)
        }
        val leftAxis = binding.stackbarchart.axisLeft
        leftAxis.apply {
            setDrawGridLines(false)
            axisMinimum = 0f
            setDrawLabels(true)
            setDrawAxisLine(true)
        }
        val rightAxis = binding.stackbarchart.axisRight
        rightAxis.apply {
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
        setupTopValue()
        setupTextViewDialog()
    }

    private fun dataProcess(){
        val barEntry = ArrayList<BarEntry>()
        val cityList = ArrayList<String>()
        val deaths = ArrayList<Float>()
        val treated = ArrayList<Float>()
        val recovered = ArrayList<Float>()

        val conditionList = ArrayList<ArrayList<Float>>()
        val filterCondition = ArrayList<ArrayList<Float>>()
        val colors = ArrayList<Int>()
        val filterColor = ArrayList<Int>()

        for (i in 0..5){
            cityList.add(listData.get(i).key)
            recovered.add(listData[i].jumlah_sembuh.toFloat())
            treated.add(listData[i].jumlah_dirawat.toFloat())
            deaths.add(listData[i].jumlah_meninggal.toFloat())
        }

        conditionList.add(recovered)
        conditionList.add(treated)
        conditionList.add(deaths)
        colors.add(Color.GREEN)
        colors.add(Color.BLUE)
        colors.add(Color.RED)

        if (removeIndex.size > 0){
            if (removeIndex.size != conditionList.size){
                for (index in removeIndex){
                    filterCondition.add(conditionList[index])
                    filterColor.add(colors[index])
                }
                for (condition in filterCondition){
                    conditionList.remove(condition)
                }
                for (color in filterColor){
                    colors.remove(color)
                }
            }
        }
        if (conditionList.size != removeIndex.size){
            for (x in 0..5){
                val values = FloatArray(conditionList.size)
                for (y in 0 until conditionList.size){
                    values[y] = conditionList[y][x]
                }
                barEntry.add(BarEntry(x.toFloat(),values))
            }
            setupStackBarChart(barEntry,colors,cityList)
            binding.linStackTotaldata.visibility = View.VISIBLE
        }else{
            binding.stackbarchart.clear()
            binding.stackbarchart.setNoDataTextColor(Color.BLACK)
            binding.stackbarchart.invalidate()
            binding.linStackTotaldata.visibility = View.GONE
        }
    }

    private fun setupTopValue(){
        myTextView = ArrayList()
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(20,10,10,10)
        for (i in 0..5) {
            val number = listData.get(i).jumlah_kasis
            myTextView.add(TextView(requireContext()))
            myTextView[i].text = formatNumber(number)
            myTextView[i].textSize = 10f
            myTextView.get(i).layoutParams = param
            myTextView[i].typeface = Typeface.DEFAULT
            myTextView[i].setTextColor(Color.BLACK)
            binding.linStackTotaldata.addView(myTextView[i])
            //Log.d("stackbar dataproses", "dataProces: i"+i)
        }


    }

    private fun setupTextViewDialog(){
        myTextView.get(0).setOnClickListener {
            val key = listData.get(0).key
            val totalData = listData.get(0).jumlah_kasis
            val totalSembuh = listData.get(0).jumlah_sembuh
            val totalDirawat = listData.get(0).jumlah_dirawat
            val totalMeninggal = listData.get(0).jumlah_meninggal
            dialogTextTotal(key,totalData, totalSembuh, totalDirawat, totalMeninggal)
        }
        myTextView.get(1).setOnClickListener {
            val key = listData.get(1).key
            val totalData = listData.get(1).jumlah_kasis
            val totalSembuh = listData.get(1).jumlah_sembuh
            val totalDirawat = listData.get(1).jumlah_dirawat
            val totalMeninggal = listData.get(1).jumlah_meninggal
            dialogTextTotal(key,totalData, totalSembuh, totalDirawat, totalMeninggal)
        }
        myTextView.get(2).setOnClickListener {
            val key = listData.get(2).key
            val totalData = listData.get(2).jumlah_kasis
            val totalSembuh = listData.get(2).jumlah_sembuh
            val totalDirawat = listData.get(2).jumlah_dirawat
            val totalMeninggal = listData.get(2).jumlah_meninggal
            dialogTextTotal(key,totalData, totalSembuh, totalDirawat, totalMeninggal)
        }
        myTextView.get(3).setOnClickListener {
            val key = listData.get(3).key
            val totalData = listData.get(3).jumlah_kasis
            val totalSembuh = listData.get(3).jumlah_sembuh
            val totalDirawat = listData.get(3).jumlah_dirawat
            val totalMeninggal = listData.get(3).jumlah_meninggal
            dialogTextTotal(key,totalData, totalSembuh, totalDirawat, totalMeninggal)
        }
        myTextView.get(4).setOnClickListener {
            val key = listData.get(4).key
            val totalData = listData.get(4).jumlah_kasis
            val totalSembuh = listData.get(4).jumlah_sembuh
            val totalDirawat = listData.get(4).jumlah_dirawat
            val totalMeninggal = listData.get(4).jumlah_meninggal
            dialogTextTotal(key,totalData, totalSembuh, totalDirawat, totalMeninggal)
        }
        myTextView.get(5).setOnClickListener {
            val key = listData.get(5).key
            val totalData = listData.get(5).jumlah_kasis
            val totalSembuh = listData.get(5).jumlah_sembuh
            val totalDirawat = listData.get(5).jumlah_dirawat
            val totalMeninggal = listData.get(5).jumlah_meninggal
            dialogTextTotal(key,totalData, totalSembuh, totalDirawat, totalMeninggal)
        }
        Log.d("setupclick", "setupTextViewDialog: "+myTextView.get(1).text)
    }
    private fun setupFilterBtn(){
        binding.linSembuhStack.setOnClickListener {
            if (!removeIndex.contains(0)){
                removeIndex.add(0)
                binding.sembuhTvStack.paintFlags = binding.sembuhTvStack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
            } else{
                binding.sembuhTvStack.paintFlags = binding.sembuhTvStack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                removeIndex.remove(0)
            }
            dataProcess()
        }

        binding.linDirawatStack.setOnClickListener {
            if (!removeIndex.contains(1)){
                removeIndex.add(1)
                binding.dirawatTvStack.paintFlags = binding.dirawatTvStack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
            } else{
                binding.dirawatTvStack.paintFlags = binding.dirawatTvStack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                removeIndex.remove(1)
            }
            dataProcess()
        }
        binding.linMeninggalStack.setOnClickListener {
            if (!removeIndex.contains(2)){
                removeIndex.add(2)
                binding.meninggalTvstack.paintFlags = binding.meninggalTvstack.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
            } else{
                binding.meninggalTvstack.paintFlags = binding.meninggalTvstack.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                removeIndex.remove(2)
            }
            dataProcess()
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

    private fun dialogTextTotal(key: String,totalData: Int, totalSembuh: Int, totalDirawat: Int, totalMeninggal: Int){
        val dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog,null)
        val builder = AlertDialog.Builder(requireContext())

        dialogView.alert_tv_totaldata.text = totalData.toString()
        dialogView.alert_tv_totalsembuh.text = totalSembuh.toString()
        dialogView.alert_tv_totaldirawat.text = totalDirawat.toString()
        dialogView.alert_tv_totalmeninggal.text = totalMeninggal.toString()
        builder.apply {
            setView(dialogView)
            setTitle(key)
            setNegativeButton("Tutup", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
        }
        builder.show()
    }

}