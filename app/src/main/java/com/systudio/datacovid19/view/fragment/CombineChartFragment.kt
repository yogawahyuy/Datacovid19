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
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
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
import kotlinx.android.synthetic.main.custom_alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_combine_chart.view.*
import kotlinx.android.synthetic.main.fragment_line_chart.view.*
import kotlinx.android.synthetic.main.fragment_line_chart.view.tv_nodata
import kotlinx.android.synthetic.main.layout_combine_chart.*
import kotlinx.android.synthetic.main.layout_combine_chart.view.*
import kotlinx.android.synthetic.main.layout_combine_chart.view.combinedChart
import kotlinx.android.synthetic.main.layout_line_chart.view.*
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
    lateinit var myTextView : ArrayList<TextView>
    private val removeIndex = arrayListOf<Int>()
    private val viewModel: MainViewModel by hiltNavGraphViewModels(R.id.main_navigation)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCombineChartBinding.inflate(inflater,container,false)
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
                //setupCombinedChart(it)
                listData = it
                setupCombinedChart()

            }else{
                binding.combineFragmentToolbar.visibility = View.GONE
                binding.nointernet.relNointernet.visibility = View.VISIBLE
            }
        }
        //viewModel.fetchAllData()
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
            animateY(2000)
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
            textSize = 11f
            setDrawGridLines(false)
            axis.valueFormatter = IndexAxisValueFormatter(label)
            axisMinimum = combinedata.xMin - 0.5f
            axisMaximum = combinedata.xMax + 0.5f
        }

        setupTopValue()
        setupTextViewDialog()
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
        myTextView = ArrayList()
        val param = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        param.setMargins(18,10,10,10)
        for (i in 0..5){
            val number = listData.get(i).jumlah_kasis
            myTextView.add(TextView(requireContext()))
            myTextView[i].text = formatNumber(number)
            myTextView[i].textSize = 10f
            myTextView[i].typeface = Typeface.DEFAULT
            myTextView[i].setTextColor(Color.BLACK)
            myTextView.get(i).layoutParams = param
            binding.linCombineTotaldata.addView(myTextView[i])
        }
    }

    private fun setupLineBtn(sembuhLineDataset: LineDataSet,dirawatLineDataSet: LineDataSet,meninggalLineDataset: LineDataSet){
        var sembuhState = 0
        binding.linCombineSembuh.setOnClickListener {
            if (!removeIndex.contains(0)){
                sembuhLineDataset.isVisible = false
                binding.combinedChart.invalidate()
                binding.sembuhTvCombine.paintFlags = binding.sembuhTvCombine.paintFlags  or (Paint.STRIKE_THRU_TEXT_FLAG)
                sembuhState = 1
                removeIndex.add(0)
            }else{
                sembuhLineDataset.isVisible = true
                combinedChart.invalidate()
                binding.sembuhTvCombine.paintFlags = binding.sembuhTvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                sembuhState = 0
                removeIndex.remove(0)
            }
            if (removeIndex.contains(0) && removeIndex.contains(1) && removeIndex.contains(2)&& removeIndex.contains(3)){
                binding.relLayCombineChart.com_tv_nodata.visibility = View.VISIBLE
                binding.relLayCombineChart.combinedChart.visibility = View.GONE
                binding.linCombineTotaldata.visibility = View.GONE
            }else{
                binding.relLayCombineChart.com_tv_nodata.visibility = View.GONE
                binding.relLayCombineChart.combinedChart.visibility = View.VISIBLE
                binding.linCombineTotaldata.visibility = View.VISIBLE
            }
        }
        var stateDirawat = 0
        binding.linCombineDirawat.setOnClickListener {
            if (!removeIndex.contains(1)){
                dirawatLineDataSet.isVisible = false
                binding.combinedChart.invalidate()
                binding.dirawatTvCombine.paintFlags = binding.dirawatTvCombine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateDirawat = 1
                removeIndex.add(1)
            }else{
                dirawatLineDataSet.isVisible = true
                binding.combinedChart.invalidate()
                binding.dirawatTvCombine.paintFlags = binding.dirawatTvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateDirawat = 0
                removeIndex.remove(1)
            }
            if (removeIndex.contains(0) && removeIndex.contains(1) && removeIndex.contains(2)&& removeIndex.contains(3)){
                binding.relLayCombineChart.com_tv_nodata.visibility = View.VISIBLE
                binding.relLayCombineChart.combinedChart.visibility = View.GONE
                binding.linCombineTotaldata.visibility = View.GONE
            }else{
                binding.relLayCombineChart.com_tv_nodata.visibility = View.GONE
                binding.relLayCombineChart.combinedChart.visibility = View.VISIBLE
                binding.linCombineTotaldata.visibility = View.VISIBLE
            }
        }
        var stateMeninggal = 0
        binding.linCombineMeninggal.setOnClickListener {
            if (!removeIndex.contains(2)) {
                meninggalLineDataset.isVisible = false
                binding.combinedChart.invalidate()
                binding.meninggalTvCombine.paintFlags = binding.meninggalTvCombine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateMeninggal = 1
                removeIndex.add(2)
            } else {
                meninggalLineDataset.isVisible = true
                binding.combinedChart.invalidate()
                binding.meninggalTvCombine.paintFlags = binding.meninggalTvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateMeninggal = 0
                removeIndex.remove(2)
            }
            if (removeIndex.contains(0) && removeIndex.contains(1) && removeIndex.contains(2)&& removeIndex.contains(3)){
                binding.relLayCombineChart.com_tv_nodata.visibility = View.VISIBLE
                binding.relLayCombineChart.combinedChart.visibility = View.GONE
                binding.linCombineTotaldata.visibility = View.GONE
            }else{
                binding.relLayCombineChart.com_tv_nodata.visibility = View.GONE
                binding.relLayCombineChart.combinedChart.visibility = View.VISIBLE
                binding.linCombineTotaldata.visibility = View.VISIBLE
            }
        }
    }

    private fun setupBarBtn(barDataSet: BarDataSet){
        var stateKasus = 0
        binding.linCombineTotalkasus.setOnClickListener {
            if (!removeIndex.contains(3)){
                barDataSet.isVisible = false
                binding.combinedChart.invalidate()
                binding.kasustvCombine.paintFlags = binding.kasustvCombine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateKasus = 1
                removeIndex.add(3)
            }else {
                barDataSet.isVisible = true
                binding.combinedChart.invalidate()
                binding.kasustvCombine.paintFlags = binding.kasustvCombine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateKasus = 0
                removeIndex.remove(3)
            }
            if (removeIndex.contains(0) && removeIndex.contains(1) && removeIndex.contains(2)&& removeIndex.contains(3)){
                binding.relLayCombineChart.com_tv_nodata.visibility = View.VISIBLE
                binding.relLayCombineChart.combinedChart.visibility = View.GONE
                binding.linCombineTotaldata.visibility = View.GONE
            }else{
                binding.relLayCombineChart.com_tv_nodata.visibility = View.GONE
                binding.relLayCombineChart.combinedChart.visibility = View.VISIBLE
                binding.linCombineTotaldata.visibility = View.VISIBLE
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
            setNegativeButton("Tutup", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
        }
        builder.show()
    }


}