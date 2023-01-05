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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.systudio.datacovid19.R
import com.systudio.datacovid19.model.ListData
import com.systudio.datacovid19.utils.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_bar_chart.*


@AndroidEntryPoint
class BarChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_chart)
        setupVM()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.barchartmenu){
            barchart.visibility = View.VISIBLE
            groupbarchart.visibility = View.GONE
            lineChart.visibility = View.GONE
            piechart.visibility = View.GONE
            stackbarchart.visibility = View.GONE
            combinedChart.visibility = View.GONE
            candleStickChart.visibility = View.GONE
        }
        if (item.itemId==R.id.groupchartmenu){
            barchart.visibility = View.GONE
            groupbarchart.visibility = View.VISIBLE
            lineChart.visibility = View.GONE
            piechart.visibility = View.GONE
            stackbarchart.visibility = View.GONE
            combinedChart.visibility = View.GONE
            candleStickChart.visibility = View.GONE
        }
        if (item.itemId==R.id.linechartmenu){
            barchart.visibility = View.GONE
            groupbarchart.visibility = View.GONE
            lineChart.visibility = View.VISIBLE
            piechart.visibility = View.GONE
            stackbarchart.visibility = View.GONE
            combinedChart.visibility = View.GONE
            candleStickChart.visibility = View.GONE
        }
        if (item.itemId==R.id.piechartmenu){
            barchart.visibility = View.GONE
            groupbarchart.visibility = View.GONE
            lineChart.visibility = View.GONE
            piechart.visibility = View.VISIBLE
            stackbarchart.visibility = View.GONE
            combinedChart.visibility = View.GONE
            candleStickChart.visibility = View.GONE
        }
        if (item.itemId == R.id.stackbarchartmenu){
            barchart.visibility = View.GONE
            groupbarchart.visibility = View.GONE
            lineChart.visibility = View.GONE
            piechart.visibility = View.GONE
            stackbarchart.visibility = View.VISIBLE
            combinedChart.visibility = View.GONE
            candleStickChart.visibility = View.GONE
        }
        if (item.itemId == R.id.combinedChartmenu){
            barchart.visibility = View.GONE
            groupbarchart.visibility = View.GONE
            lineChart.visibility = View.GONE
            piechart.visibility = View.GONE
            stackbarchart.visibility = View.GONE
            combinedChart.visibility = View.VISIBLE
            candleStickChart.visibility = View.GONE
        }
        if (item.itemId == R.id.candlestickmenu){
            barchart.visibility = View.GONE
            groupbarchart.visibility = View.GONE
            lineChart.visibility = View.GONE
            piechart.visibility = View.GONE
            stackbarchart.visibility = View.GONE
            combinedChart.visibility = View.GONE
            candleStickChart.visibility = View.VISIBLE
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupVM(){
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.fetchLiveData().observe(this, Observer {
            if (it!=null){
                setupBarchart(it)
                setupGroupChart(it)
                setupLineChart(it)
                setupPieChart(it)
                setupStackedBarChart(it)
                setupCombinedChart(it)
                setupCandleStickChart(it)
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
    }

    private fun setupStackedBarChart(listData: List<ListData>){
        val legend = stackbarchart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL

        val sembuh = ArrayList<BarEntry>()
        for (i in 0..5 ){
            sembuh.add(BarEntry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
        }
        val meninggal = ArrayList<BarEntry>()
        for (i in 0..5 ){
            meninggal.add(BarEntry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
        }
        val dirawat = ArrayList<BarEntry>()
        for (i in 0..5 ){
            dirawat.add(BarEntry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
        }

        val sembuhBarDataSet = BarDataSet(sembuh,"Sembuh")
        sembuhBarDataSet.color = Color.GREEN
        val meninggalBarDataSet = BarDataSet(meninggal,"Meninggal")
        meninggalBarDataSet.color = Color.RED
        val dirawatBarDataSet = BarDataSet(dirawat,"dirawat")
        dirawatBarDataSet.color = Color.BLUE

        stackbarchart.description.isEnabled = false
        stackbarchart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        stackbarchart.setTouchEnabled(true)
        stackbarchart.isDragEnabled = true
        stackbarchart.setScaleEnabled(true)
        stackbarchart.data = BarData(sembuhBarDataSet,dirawatBarDataSet,meninggalBarDataSet)
        stackbarchart.animateXY(200,500)
    }

    private fun setupLineChart(listData: List<ListData>){
//        val kasus = ArrayList<Entry>()
//        for (i in 0..5 ){
//            kasus.add(BarEntry(i.toFloat(),listData.get(i).jumlah_kasis.toFloat()))
//        }
        val sembuh = ArrayList<Entry>()
        for (i in 0..5 ){
            Log.d("setuplinechart", "setupLineChart: "+listData.get(i).jumlah_sembuh)
            sembuh.add(Entry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
        }
        val meninggal = ArrayList<Entry>()
        for (i in 0..5 ){
            meninggal.add(Entry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
        }
        val dirawat = ArrayList<Entry>()
        for (i in 0..5 ){
            dirawat.add(Entry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
        }

//        val kasusLineDataSet = LineDataSet(kasus,"Kasus")
//        kasusLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
//        kasusLineDataSet.color = Color.BLUE
//        kasusLineDataSet.circleRadius = 5f
//        kasusLineDataSet.setCircleColor(Color.BLUE)

        val sembuhLineDataset = LineDataSet(sembuh,"Sembuh")
        sembuhLineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        sembuhLineDataset.color = Color.GREEN
        sembuhLineDataset.circleRadius = 5f
        sembuhLineDataset.setCircleColor(Color.GREEN)

        val dirawatLineDataSet = LineDataSet(dirawat,"dirawat")
        dirawatLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dirawatLineDataSet.color = Color.GRAY
        dirawatLineDataSet.circleRadius = 5f
        dirawatLineDataSet.setCircleColor(Color.GRAY)

        val meninggalLineDataset = LineDataSet(meninggal,"Meninggal")
        meninggalLineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        meninggalLineDataset.color = Color.RED
        meninggalLineDataset.circleRadius = 5f
        meninggalLineDataset.setCircleColor(Color.RED)

        val legend = lineChart.legend
        legend.isEnabled = true
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP)
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER)
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL)
        legend.setDrawInside(false)

        lineChart.description.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.data = LineData( sembuhLineDataset, dirawatLineDataSet, meninggalLineDataset)
        lineChart.animateXY(100, 500)

        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }
        lineChart.xAxis?.valueFormatter = IndexAxisValueFormatter(label)

    }

    private fun setupPieChart(listData: List<ListData>){
        piechart.description.isEnabled = false
        piechart.setExtraOffsets(5f,10f,5f,5f)
        piechart.dragDecelerationFrictionCoef=0.95f
        piechart.centerText = "Jumlah Sembuh"
        piechart.setCenterTextColor(Color.BLACK)
        piechart.setDrawCenterText(true)

        piechart.isDrawHoleEnabled = true
        piechart.setHoleColor(Color.WHITE)

        piechart.setTransparentCircleAlpha(110)
        piechart.setTransparentCircleColor(Color.WHITE)

        piechart.holeRadius =58f
        piechart.transparentCircleRadius = 61f

        piechart.rotationAngle = 0f

        piechart.isRotationEnabled = true
        piechart.isHighlightPerTapEnabled = true

        piechart.animateY(1000,Easing.EaseInOutQuad)
        piechart.legend.isEnabled = false
        piechart.setEntryLabelColor(Color.WHITE)
        piechart.setEntryLabelTextSize(0f)

        piechart.highlightValue(null)

        val sembuh = ArrayList<PieEntry>()
        for (i in 0..5 ){
            sembuh.add(PieEntry(listData.get(i).jumlah_sembuh.toFloat(),listData.get(i).key))
        }

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

        val legend = piechart.legend
        piechart.legend.isWordWrapEnabled = true
        piechart.legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.formSize = 20f
        legend.formToTextSpace = 0f
        legend.form = Legend.LegendForm.CIRCLE
        legend.textSize = 10f
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.isWordWrapEnabled = true
        legend.setDrawInside(false)

        val data = PieData(sembuhPieDataSet)
        piechart.data = data

    }

    private fun setupCombinedChart(listData: List<ListData>){
        combinedChart.description.isEnabled = false
        combinedChart.setDrawGridBackground(false)
        combinedChart.setDrawBarShadow(false)
        combinedChart.isHighlightFullBarEnabled = false
        combinedChart.setTouchEnabled(true)
        combinedChart.setPinchZoom(true)
        combinedChart.isDragXEnabled = true

        val label = ArrayList<String>()
        for (i in 0..5){
            label.add(listData.get(i).key)
        }

        val legend = combinedChart.legend
        legend.isWordWrapEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        val rightAxis = combinedChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.axisMinimum = 0f

        val leftAxis = combinedChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 0f

        val axis = combinedChart.xAxis
        axis.position = XAxis.XAxisPosition.BOTTOM
        axis.axisMinimum = 0f
        axis.granularity = 1f
        axis.valueFormatter = IndexAxisValueFormatter(label)

        val combinedata = CombinedData()

        combinedata.setData(generateBarData(listData))
        combinedata.setData(generateLineData(listData))

        combinedChart.data = combinedata
    }

    private fun generateLineData(listData: List<ListData>) : LineData{
        val linedata = LineData()
        val sembuh = ArrayList<Entry>()
        for (i in 0..5 ){
            sembuh.add(Entry(i.toFloat(),listData.get(i).jumlah_sembuh.toFloat()))
        }
        val meninggal = ArrayList<Entry>()
        for (i in 0..5 ){
            meninggal.add(Entry(i.toFloat(),listData.get(i).jumlah_meninggal.toFloat()))
        }
        val dirawat = ArrayList<Entry>()
        for (i in 0..5 ){
            dirawat.add(Entry(i.toFloat(),listData.get(i).jumlah_dirawat.toFloat()))
        }

        val sembuhLineDataset = LineDataSet(sembuh,"Sembuh")
        sembuhLineDataset.mode = LineDataSet.Mode.CUBIC_BEZIER
        sembuhLineDataset.color = Color.GREEN
        sembuhLineDataset.circleRadius = 5f
        sembuhLineDataset.setCircleColor(Color.GREEN)

        val dirawatLineDataSet = LineDataSet(dirawat,"dirawat")
        dirawatLineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dirawatLineDataSet.color = Color.GRAY
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
        barDataset.color = Color.BLUE
        val meninggalDataSet = BarDataSet(meninggal,"")
        meninggalDataSet.color = Color.RED

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