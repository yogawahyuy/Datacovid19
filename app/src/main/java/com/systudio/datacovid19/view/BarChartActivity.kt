package com.systudio.datacovid19.view

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ColorFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.StackedValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.systudio.datacovid19.R
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import com.systudio.datacovid19.utils.marker.BarChartMarkerView
import com.systudio.datacovid19.utils.marker.LineChartMarker
import com.systudio.datacovid19.utils.marker.StackBarChartMarker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*
import kotlinx.android.synthetic.main.activity_bar_chart.view.*
import kotlinx.android.synthetic.main.custom_legend_filter.*
import kotlinx.android.synthetic.main.custom_legend_filter.dirawatTv
import kotlinx.android.synthetic.main.custom_legend_filter.lin_dirawat
import kotlinx.android.synthetic.main.custom_legend_filter.lin_meninggal
import kotlinx.android.synthetic.main.custom_legend_filter.meninggalTv
import kotlinx.android.synthetic.main.custom_legend_filter.rel_custom_legend
import kotlinx.android.synthetic.main.custom_legend_filter.sembuhTv
import kotlinx.android.synthetic.main.custom_totaldata_chart.*
import kotlinx.android.synthetic.main.layout_combine_chart.*
import kotlinx.android.synthetic.main.layout_combine_chart.view.*
import kotlinx.android.synthetic.main.layout_line_chart.*
import kotlinx.android.synthetic.main.layout_line_chart.view.*
import kotlinx.android.synthetic.main.layout_pie_chart.view.*
import kotlinx.android.synthetic.main.layout_stackbar_chart.*
import kotlinx.android.synthetic.main.layout_stackbar_chart.view.*


@AndroidEntryPoint
class BarChartActivity : AppCompatActivity() {
    lateinit var ld : List<ListData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)
        setupVM()
        rel_custom_legend.visibility = View.GONE
        tv_totaldata.visibility = View.GONE
        tv_totalperprovinsi.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.barchartmenu){
            cardview_barchart.visibility = View.VISIBLE
            layout_combinechart.visibility = View.GONE
            layout_stackbar.visibility = View.GONE
            layout_piechart.visibility = View.GONE
            layout_linechart.visibility = View.GONE
        }
       // if (item.itemId==R.id.groupchartmenu){ }
        if (item.itemId==R.id.linechartmenu){
            cardview_barchart.visibility = View.GONE
            layout_combinechart.visibility = View.GONE
            layout_stackbar.visibility = View.GONE
            layout_piechart.visibility = View.GONE
            layout_linechart.visibility = View.VISIBLE
            setupLineChart(ld)
        }
        if (item.itemId==R.id.piechartmenu){
            cardview_barchart.visibility = View.GONE
            layout_combinechart.visibility = View.GONE
            layout_stackbar.visibility = View.GONE
            layout_piechart.visibility = View.VISIBLE
            layout_linechart.visibility = View.GONE
            setupPieChart(ld)
        }
        if (item.itemId == R.id.stackbarchartmenu){
            cardview_barchart.visibility = View.GONE
            layout_combinechart.visibility = View.GONE
            layout_stackbar.visibility = View.VISIBLE
            layout_piechart.visibility = View.GONE
            layout_linechart.visibility = View.GONE
            setupStackedBarChart(ld)
        }
        if (item.itemId == R.id.combinedChartmenu){
            cardview_barchart.visibility = View.GONE
            layout_combinechart.visibility = View.VISIBLE
            layout_stackbar.visibility = View.GONE
            layout_piechart.visibility = View.GONE
            layout_linechart.visibility = View.GONE
            setupCombinedChart(ld)
        }
        //if (item.itemId == R.id.candlestickmenu){ }
        return super.onOptionsItemSelected(item)
    }
    private fun setupVM(){
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(this, Observer {
            if (it!=null){
                setupBarchart(it)
                ld = it
            }
        })
        viewModel.fetchAllData()
    }

    private fun setupBarchart(listData :List<ListData>){
        val barEntriesList = ArrayList<BarEntry>()

        for (i in 0..5 ){
            Log.d("setupbarchart", "setupBarchart: "+listData.get(i).jumlah_kasis)
                barEntriesList.add(BarEntry(i.toFloat(),listData.get(i).jumlah_kasis.toFloat()))
        }
        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        val barDataSet = BarDataSet(barEntriesList,"Provinsi")
        barDataSet.color = Color.BLUE
        barchart.description.isEnabled = false
        barchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barchart.data = BarData(barDataSet)
        barchart.animateXY(100,500)
        barchart.xAxis?.valueFormatter = IndexAxisValueFormatter(label)

        val legend = barchart.legend
        legend.isEnabled = false
        val rightAxis = barchart.axisRight
        rightAxis.isEnabled = false
        val marker = BarChartMarkerView(this,R.layout.custom_marker_view,label)
        barchart.marker = marker

        //barChart.invalidate()

    }

    private fun setupGroupChart(listData: List<ListData>){
        groupbarchart.setTouchEnabled(true)
        groupbarchart.setPinchZoom(true)
        groupbarchart.isDragEnabled = true
        groupbarchart.description.isEnabled = false
        groupbarchart.xAxis?.position = XAxis.XAxisPosition.BOTTOM
        groupbarchart.xAxis?.granularity = 1f
        groupbarchart.xAxis?.setCenterAxisLabels(true)
        groupbarchart.xAxis?.setDrawGridLines(true)
        groupbarchart.xAxis?.spaceMin = 0f

        groupbarchart.axisLeft.axisMinimum = 0f
        groupbarchart.axisLeft.spaceTop = 30f
        groupbarchart.axisLeft.setDrawGridLines(true)
        groupbarchart.axisRight.isEnabled = false

        val legend = barchart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(true)

        val kasus = ArrayList<BarEntry>()
        for (i in 0..5){
            kasus.add(BarEntry(i.toFloat(),listData.get(i).jumlah_kasis.toFloat()))
        }
        val sembuh = ArrayList<BarEntry>()
        for (i in 0..5){
            sembuh.add(BarEntry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
        }
        val meninggal = ArrayList<BarEntry>()
        for (i in 0..5){
            meninggal.add(BarEntry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
        }
        val dirawat = ArrayList<BarEntry>()
        for (i in 0..5 ){
            dirawat.add(BarEntry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
        }

        val kasusBarDataSet = BarDataSet(kasus,"Kasus")
        kasusBarDataSet.color = Color.BLUE
        val sembuhBarDataSet = BarDataSet(sembuh,"Sembuh")
        sembuhBarDataSet.color = Color.GREEN
        val dirawatBarDataSet = BarDataSet(dirawat,"Dirawat")
        dirawatBarDataSet.color = Color.GRAY
        val meninggalBarDataSet = BarDataSet(meninggal,"Meninggal")
        meninggalBarDataSet.color = Color.RED

        val groupBar = BarData(sembuhBarDataSet,dirawatBarDataSet,meninggalBarDataSet,kasusBarDataSet)
        groupBar.barWidth = 0.25f
        groupbarchart.data = groupBar
        groupbarchart.groupBars(0f,0.07f,0.03f)
        groupbarchart.animateXY(100,500)

        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        groupbarchart.xAxis?.valueFormatter = IndexAxisValueFormatter(label)
        val marker = BarChartMarkerView(this,R.layout.custom_marker_view,label)
        groupbarchart.marker = marker
    }

    private fun setupStackedBarChart(listData: List<ListData>){
        val legend = layout_stackbar.stackbarchart.legend
        legend.isEnabled = false

        var totalSembuh = 0
        var totalMeninggal = 0
        var totalDirawat = 0
        var totalKasus = 0
        val sembuh = ArrayList<BarEntry>()
        for (i in 0..5 ){
            sembuh.add(BarEntry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
            totalSembuh += listData.get(i).jumlah_sembuh
        }
        val meninggal = ArrayList<BarEntry>()
        for (i in 0..5 ){
            meninggal.add(BarEntry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
            totalMeninggal += listData.get(i).jumlah_meninggal
        }
        val dirawat = ArrayList<BarEntry>()
        for (i in 0..5 ){
            dirawat.add(BarEntry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            totalDirawat += listData.get(i).jumlah_dirawat
        }
        //add total on top chart
        totalKasus += totalSembuh + totalDirawat + totalMeninggal
        //val totaldatatext = "total sembuh : " + totalSembuh+ ", Total dirawat : " + totalDirawat +", Total Meninggal : "+totalMeninggal+", Total Kasus : "+totalKasus
        tv_stack_totaldata.text =  "Total Kasus :"
        val isiTv = "DKI Jakarta : " + listData.get(0).jumlah_kasis + ", Jawa Barat : "+ listData.get(1).jumlah_kasis+", Jawa Tengah : "+ listData.get(2).jumlah_kasis +
                ", Jawa Timur : "+ listData.get(3).jumlah_kasis + ", Banten : "+ listData.get(4).jumlah_kasis +", Yogyakarta : "+listData.get(5).jumlah_kasis
        tv_stack_totalperprovinsi.text = isiTv
        val sembuhBarDataSet = BarDataSet(sembuh,"Sembuh")
        sembuhBarDataSet.color = Color.GREEN
        val meninggalBarDataSet = BarDataSet(meninggal,"Meninggal")
        meninggalBarDataSet.color = Color.RED
        val dirawatBarDataSet = BarDataSet(dirawat,"dirawat")
        dirawatBarDataSet.color = Color.BLUE
        dirawatBarDataSet.setDrawValues(false)

        layout_stackbar.stackbarchart.description.isEnabled = false
        layout_stackbar.stackbarchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        layout_stackbar.stackbarchart.axisRight.isEnabled = false
        layout_stackbar.stackbarchart.axisLeft.axisMinimum = 0f
        layout_stackbar.stackbarchart.setTouchEnabled(true)
        layout_stackbar.stackbarchart.isDragEnabled = true
        layout_stackbar.stackbarchart.setScaleEnabled(true)
        layout_stackbar.stackbarchart.data = BarData(sembuhBarDataSet,dirawatBarDataSet,meninggalBarDataSet)
        layout_stackbar.stackbarchart.animateXY(200,500)

        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        layout_stackbar.stackbarchart.xAxis?.valueFormatter = IndexAxisValueFormatter(label)
        layout_stackbar.stackbarchart.xAxis.isGranularityEnabled = true
        layout_stackbar.stackbarchart.xAxis.granularity = 2f
        val marker = StackBarChartMarker(this,R.layout.custom_marker_view,label)
        layout_stackbar.stackbarchart.marker = marker

        // legend filter
        var state = 0
        lin_sembuh_stack.setOnClickListener {
            if (state==0) {
                sembuhBarDataSet.isVisible = false
                layout_stackbar.stackbarchart.invalidate()
                sembuhTv_stack.paintFlags = sembuhTv.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                state = 1
            }else {
                sembuhBarDataSet.isVisible = true
                layout_stackbar.stackbarchart.invalidate()
                sembuhTv_stack.paintFlags = sembuhTv.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                state = 0
            }
        }
        var stateDirawat = 0
        lin_dirawat_stack.setOnClickListener {
            if (stateDirawat==0){
                dirawatBarDataSet.isVisible = false
                layout_stackbar.stackbarchart.invalidate()
                dirawatTv_stack.paintFlags = dirawatTv.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateDirawat = 1
            }else{
                dirawatBarDataSet.isVisible = true
                layout_stackbar.stackbarchart.invalidate()
                dirawatTv_stack.paintFlags = dirawatTv.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateDirawat = 0
            }
        }
        var stateMeninggal = 0
        lin_meninggal_stack.setOnClickListener {
            if (stateMeninggal==0){
                meninggalBarDataSet.isVisible = false
                layout_stackbar.stackbarchart.invalidate()
                meninggalTvstack.paintFlags = meninggalTv.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateMeninggal = 1
            }else{
                meninggalBarDataSet.isVisible = true
                layout_stackbar.stackbarchart.invalidate()
                meninggalTvstack.paintFlags = meninggalTv.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateMeninggal = 0
            }
        }
    }

    private fun setupLineChart(listData: List<ListData>){
        var totalSembuh = 0
        var totalMeninggal = 0
        var totalDirawat = 0
        var totalKasus = 0
        val sembuh = ArrayList<Entry>()
        for (i in 0..5 ){
            Log.d("setuplinechart", "setupLineChart: "+listData.get(i).jumlah_sembuh)
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
        totalKasus += totalSembuh + totalDirawat + totalMeninggal
        tv_line_totalperprovinsi.visibility = View.GONE
        tv_line_totaldata.text = "total sembuh : " + totalSembuh+ ", Total dirawat : " + totalDirawat +", Total Meninggal : "+totalMeninggal+", Total Kasus : "+totalKasus

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

        val legend = layout_linechart.lineChart.legend
        legend.isEnabled = false
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        layout_linechart.lineChart.description.isEnabled = false
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.xAxis.granularity = 2f
        layout_linechart.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        layout_linechart.lineChart.data = LineData( sembuhLineDataset, dirawatLineDataSet, meninggalLineDataset)
        layout_linechart.lineChart.animateXY(100, 500)
        val rightAxis = layout_linechart.lineChart.axisRight
        rightAxis.isEnabled = false


        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        layout_linechart.lineChart.xAxis?.valueFormatter = IndexAxisValueFormatter(label)
        val marker = LineChartMarker(this,R.layout.custom_marker_view)
        layout_linechart.lineChart.marker = marker

        //legend filter
        var state = 0
        lin_chartsembuh.setOnClickListener {
            if (state==0) {
                sembuhLineDataset.isVisible = false
                layout_linechart.lineChart.invalidate()
                sembuhTvline.paintFlags = sembuhTv.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                state = 1
            }else {
                sembuhLineDataset.isVisible = true
                layout_linechart.lineChart.invalidate()
                sembuhTvline.paintFlags = sembuhTv.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                state = 0
            }
        }
        var stateDirawat = 0
        lin_chartdirawat.setOnClickListener {
            if (stateDirawat==0){
                dirawatLineDataSet.isVisible = false
                layout_linechart.lineChart.invalidate()
                dirawatTvline.paintFlags = dirawatTv.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateDirawat = 1
            }else{
                dirawatLineDataSet.isVisible = true
                layout_linechart.lineChart.invalidate()
                dirawatTvline.paintFlags = dirawatTv.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateDirawat = 0
            }
        }
        var stateMeninggal = 0
        lin_chartmeninggal.setOnClickListener {
            if (stateMeninggal==0){
                meninggalLineDataset.isVisible = false
                layout_linechart.lineChart.invalidate()
                meninggalTvstack.paintFlags = meninggalTv.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateMeninggal = 1
            }else{
                meninggalLineDataset.isVisible = true
                layout_linechart.lineChart.invalidate()
                meninggalTvstack.paintFlags = meninggalTv.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateMeninggal = 0
            }
        }

    }

    private fun setupPieChart(listData: List<ListData>){
        layout_piechart.piechart.description.isEnabled = false
        layout_piechart.piechart.setExtraOffsets(5f,10f,5f,5f)
        layout_piechart.piechart.dragDecelerationFrictionCoef=0.95f
        layout_piechart.piechart.centerText = "Jumlah Sembuh"
        layout_piechart.piechart.setCenterTextColor(Color.BLACK)
        layout_piechart.piechart.setDrawCenterText(true)

        layout_piechart.piechart.isDrawHoleEnabled = true
        layout_piechart.piechart.setHoleColor(Color.WHITE)

        layout_piechart.piechart.setTransparentCircleAlpha(110)
        layout_piechart.piechart.setTransparentCircleColor(Color.WHITE)

        layout_piechart.piechart.holeRadius =58f
        layout_piechart.piechart.transparentCircleRadius = 61f

        layout_piechart.piechart.rotationAngle = 0f

        layout_piechart.piechart.isRotationEnabled = true
        layout_piechart.piechart.isHighlightPerTapEnabled = true

        layout_piechart.piechart.animateY(1000,Easing.EaseInOutQuad)
        layout_piechart.piechart.legend.isEnabled = false
        layout_piechart.piechart.setEntryLabelColor(Color.WHITE)
        layout_piechart.piechart.setEntryLabelTextSize(0f)

        layout_piechart.piechart.highlightValue(null)

        var totalSembuh = 0
        val sembuh = ArrayList<PieEntry>()
        for (i in 0..5 ){
            sembuh.add(PieEntry(listData.get(i).jumlah_sembuh.toFloat(),listData.get(i).key))
            totalSembuh += listData.get(i).jumlah_sembuh
        }
        tv_totaldata.text = "Total Sembuh : "+totalSembuh
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

        val legend = layout_piechart.piechart.legend
        layout_piechart.piechart.legend.isWordWrapEnabled = true
        layout_piechart.piechart.legend.isEnabled = false
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.formSize = 20f
        legend.formToTextSpace = 0f
        legend.form = Legend.LegendForm.CIRCLE
        legend.textSize = 10f
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)



        val data = PieData(sembuhPieDataSet)
        layout_piechart.piechart.data = data

    }

    private fun setupCombinedChart(listData: List<ListData>){
        layout_combinechart.combinedChart.description.isEnabled = false
        layout_combinechart.combinedChart.setDrawGridBackground(false)
        layout_combinechart.combinedChart.setDrawBarShadow(false)
        layout_combinechart.combinedChart.isHighlightFullBarEnabled = false
        layout_combinechart.combinedChart.setTouchEnabled(true)
        layout_combinechart.combinedChart.setPinchZoom(true)
        layout_combinechart.combinedChart.isDragXEnabled = true

        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }

        val legend = layout_combinechart.combinedChart.legend
        legend.isEnabled = false
        legend.isWordWrapEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)


        val rightAxis = layout_combinechart.combinedChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.axisMinimum = 0f

        val leftAxis = layout_combinechart.combinedChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 0f

        val axis = layout_combinechart.combinedChart.xAxis
        axis.position = XAxis.XAxisPosition.BOTTOM
        axis.axisMinimum = 0f
        axis.granularity = 2f
        axis.valueFormatter = IndexAxisValueFormatter(label)

        val combinedata = CombinedData()

        combinedata.setData(generateBarData(listData))
        combinedata.setData(generateLineData(listData))

        layout_combinechart.combinedChart.data = combinedata

    }

    private fun generateLineData(listData: List<ListData>) : LineData{
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
        tv_combine_totalperprovinsi.visibility=View.GONE
        tv_combine_totaldata.text = "total sembuh : " + totalSembuh+ ", Total dirawat : " + totalDirawat +", Total Meninggal : "+totalMeninggal+", Total Kasus : "+totalKasus
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
        lin_combine_sembuh.setOnClickListener {
            if (sembuhState==0){
                sembuhLineDataset.isVisible = false
                combinedChart.invalidate()
                sembuhTv_combine.paintFlags = sembuhTv_combine.paintFlags  or (Paint.STRIKE_THRU_TEXT_FLAG)
                sembuhState = 1
            }else{
                sembuhLineDataset.isVisible = true
                combinedChart.invalidate()
                sembuhTv_combine.paintFlags = sembuhTv_combine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                sembuhState = 0
            }
            var stateDirawat = 0
            lin_combine_dirawat.setOnClickListener {
                if (stateDirawat==0){
                    dirawatLineDataSet.isVisible = false
                    combinedChart.invalidate()
                    dirawatTv_combine.paintFlags = dirawatTv_combine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                    stateDirawat = 1
                }else{
                    dirawatLineDataSet.isVisible = true
                    combinedChart.invalidate()
                    dirawatTv_combine.paintFlags = dirawatTv_combine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                    stateDirawat = 0
                }
            }
            var stateMeninggal = 0
            lin_combine_meninggal.setOnClickListener {
                if (stateMeninggal==0){
                    meninggalLineDataset.isVisible = false
                    combinedChart.invalidate()
                    meninggalTv_combine.paintFlags = meninggalTv_combine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                    stateMeninggal = 1
                }else{
                    meninggalLineDataset.isVisible = true
                    combinedChart.invalidate()
                    meninggalTv_combine.paintFlags = meninggalTv_combine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
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
        lin_combine_totalkasus.setOnClickListener {
            if (stateKasus==0){
                barDataset.isVisible = false
                combinedChart.invalidate()
                kasustv_combine.paintFlags = kasustv_combine.paintFlags or (Paint.STRIKE_THRU_TEXT_FLAG)
                stateKasus = 1
            }else {
                barDataset.isVisible = true
                combinedChart.invalidate()
                kasustv_combine.paintFlags = kasustv_combine.paintFlags and (Paint.ANTI_ALIAS_FLAG)
                stateKasus = 0
            }
        }

        return BarData(barDataset)
    }

    private fun setupCandleStickChart(listData: List<ListData>){
        candleStickChart.setMaxVisibleValueCount(60)
       // candleStickChart.setDrawBorders(true)
        //candleStickChart.isHighlightPerDragEnabled = true
        candleStickChart.setBorderColor(Color.LTGRAY)
        val axisLeft = candleStickChart.axisLeft
        val rightAxis = candleStickChart.axisRight

        axisLeft.setDrawGridLines(false)
        axisLeft.setLabelCount(7,false)
        rightAxis.isEnabled = false
        candleStickChart.requestDisallowInterceptTouchEvent(true)

        val xAxis = candleStickChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawLabels(false)
        rightAxis.textColor = Color.WHITE
        axisLeft.setDrawLabels(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.setAvoidFirstLastClipping(true)

        val legend = candleStickChart.legend
        legend.isEnabled = false

        val candleEntry = ArrayList<CandleEntry>()
        for (i in 0..5 ){
//            candleEntry.add(CandleEntry(i.toFloat(),listData.get(i).jumlah_kasis.toFloat(),listData.get(i).jumlah_sembuh.toFloat(),
//            listData.get(i).jumlah_sembuh.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
            candleEntry.add(CandleEntry(0f, 225f, 219.84f, 224.94f, 221.07f))
            candleEntry.add(CandleEntry(1f, 228.35f, 222.57f, 223.52f, 226.41f))
            candleEntry.add(CandleEntry(2f, 226.84f, 222.52f, 225.75f, 223.84f))
            candleEntry.add(CandleEntry(3f, 222.95f, 217.27f, 222.15f, 217.88f))
        }

        val candleDataSet = CandleDataSet(candleEntry,"Dataset1")
        candleDataSet.axisDependency = YAxis.AxisDependency.LEFT
        //candleDataSet.color = Color.rgb(80, 80, 80)
        candleDataSet.shadowColor = Color.LTGRAY
        candleDataSet.shadowWidth = 0.8f
        candleDataSet.decreasingColor = Color.RED
        candleDataSet.decreasingPaintStyle = Paint.Style.FILL
        candleDataSet.increasingColor = Color.rgb(122, 242, 84)
        candleDataSet.increasingPaintStyle = Paint.Style.STROKE
        candleDataSet.neutralColor = Color.BLUE

        val data = CandleData(candleDataSet)

        candleStickChart.data = data
    }

}