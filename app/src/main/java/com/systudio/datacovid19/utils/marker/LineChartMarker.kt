package com.systudio.datacovid19.utils.marker

import android.content.Context
import android.view.View
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.custom_marker_view.view.*

class LineChartMarker(context: Context, layout: Int) : MarkerView(context,layout) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val xAxis = e?.x?.toInt() ?: 0
       // tvmarkername?.text = label[xAxis].label
        tvmarkername?.visibility = View.GONE
        tvmarkerdata?.text = e?.y.toString()
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}