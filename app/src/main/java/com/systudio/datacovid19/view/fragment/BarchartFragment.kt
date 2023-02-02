package com.systudio.datacovid19.view.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.systudio.datacovid19.R
import com.systudio.datacovid19.databinding.FragmentBarchartBinding
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.utils.MyAxisValueFormatter
import com.systudio.datacovid19.utils.marker.BarChartMarkerView
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.activity_bar_chart.barchart
import kotlinx.android.synthetic.main.fragment_barchart.*
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 * Use the [BarchartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BarchartFragment : Fragment() {

    private var _binding : FragmentBarchartBinding? = null
    private val binding get() = _binding!!
    lateinit var myTextView: ArrayList<TextView>
    private lateinit var listData: List<ListData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBarchartBinding.inflate(inflater,container,false)
        initVm()
        return binding.root
    }


    private fun initVm(){
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                listData = it
                dataProses()
                setupTopValue()
            }else{
                binding.cardviewBarchart.visibility = View.GONE
                binding.nointernet.relNointernet.visibility = View.VISIBLE
            }
        }
        viewModel.fetchAllData()
    }

    private fun setupBarChart(entries : List<BarEntry>,label : List<String>){
        val barMarker = BarChartMarkerView(requireActivity(),R.layout.custom_marker_view,label as ArrayList<String>)
        val dataSet = BarDataSet(entries,"")

        dataSet.setDrawValues(false)
        val barChart = binding.barchartFra
        barChart.apply {
            description.isEnabled = false
            data = BarData(dataSet)
            animateY(1000)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            legend.isEnabled = false
            marker = barMarker
        }
        val xAxis = binding.barchartFra.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 2f
            setDrawGridLines(false)
            valueFormatter = IndexAxisValueFormatter(label)
        }
        //setupTopValue()
    }

    private fun dataProses(){
        val barEntriesList = ArrayList<BarEntry>()
        val label = ArrayList<String>()
        for (i in 0..5){
            barEntriesList.add(BarEntry(i.toFloat(),listData.get(i).jumlah_kasis.toFloat()))
            label.add(listData.get(i).key)
        }
        setupBarChart(barEntriesList,label)
    }

    private fun setupTopValue(){
        myTextView = ArrayList()
        val param = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        param.setMargins(18,10,10,10)
        for (i in 0..5) {
            val number = listData.get(i).jumlah_kasis
            myTextView.add(TextView(requireContext()))
            myTextView[i].text = formatNumber(number)
            myTextView[i].textSize = 10f
            myTextView.get(i).layoutParams = param
            myTextView[i].typeface = Typeface.DEFAULT
            myTextView[i].setTextColor(Color.BLACK)
            myTextView[i].setOnClickListener { dialogTextTotal() }
            binding.linTopValue.addView(myTextView[i])
        }
        Log.d("barchart", "setupTopValue: "+myTextView.size)
    }

    private fun dialogTextTotal(){
        val dialogView = layoutInflater.inflate(R.layout.custom_alert_dialog,null)
        val builder = AlertDialog.Builder(requireContext())

        builder.setView(dialogView)
        builder.show()
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