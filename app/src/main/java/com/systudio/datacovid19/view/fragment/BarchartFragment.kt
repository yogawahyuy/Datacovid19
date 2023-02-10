package com.systudio.datacovid19.view.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginStart
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
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
import com.systudio.datacovid19.view.MainActivity
import com.systudio.datacovid19.view.TreeMapChartActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.activity_bar_chart.barchart
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_barchart.*
import kotlinx.android.synthetic.main.fragment_barchart.view.*
import java.text.DecimalFormat

/**
 * A simple [Fragment] subclass.
 * Use the [BarchartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class BarchartFragment : Fragment() {

    private var _binding : FragmentBarchartBinding? = null
    private val binding get() = _binding!!
    lateinit var myTextView: ArrayList<TextView>
    private lateinit var listData: List<ListData>
    private var activity: MainActivity?=null
    private val viewModel: MainViewModel by hiltNavGraphViewModels(R.id.main_navigation)
    private var data : Int =0



    override fun onCreate(savedInstanceState: Bundle?) {
        //activity = activity
        //initVm()
        //Toast.makeText(requireContext(),"OnCreate barchart",Toast.LENGTH_LONG).show()
        //viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBarchartBinding.inflate(inflater,container,false)
        //initVm()
        //Toast.makeText(requireContext(),"OnCreateView barchart created",Toast.LENGTH_LONG).show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVm()
        //dataProses()
        //setupTopValue()
//        if (savedInstanceState!=null){
//            Toast.makeText(requireContext(),"OnViewCreated barchart done created",Toast.LENGTH_LONG).show()
//        }else {
//            Toast.makeText(
//                requireContext(),
//                "OnViewCreated barchart done recreated",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//        binding.relChart.tv_to_tremap.setOnClickListener {
//            val intent = Intent(requireContext(),TreeMapChartActivity::class.java)
//            startActivity(intent)
//        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Toast.makeText(requireContext(),"ondestroy barchart",Toast.LENGTH_SHORT).show()
        //_binding=null
    }

    private fun initVm(){
        //val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(requireActivity()) {
            if (it != null) {
                listData = it
                dataProses()
                setupTopValue()
            }else{
                binding.cardviewBarchart.visibility = View.GONE
               // binding.nointernet.relNointernet.visibility = View.VISIBLE
            }
        }
        //viewModel.fetchAllData()
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
        param.setMargins(20,10,10,10)
        for (i in 0..5) {
            val number = listData.get(i).jumlah_kasis
            myTextView.add(TextView(requireContext()))
            myTextView[i].text = formatNumber(number)
            myTextView[i].textSize = 10f
            myTextView.get(i).layoutParams = param
            myTextView[i].typeface = Typeface.DEFAULT
            myTextView[i].setTextColor(Color.BLACK)
            binding.linTopValue.addView(myTextView[i])
        }
        Log.d("barchart", "setupTopValue: "+myTextView.size)
        setupTextViewDialog()
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
            setNegativeButton("Tutup",DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
        }
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