package com.systudio.datacovid19.utils.marker

import android.content.Context
import android.content.res.XmlResourceParser
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.custom_marker_view.view.*

class StackBarChartMarker(context: Context, layout: Int, private val label: ArrayList<String>) : MarkerView(context,layout) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val xAxis = e?.x?.toInt() ?: 0
        tvmarkername?.text = label[xAxis]
        tvmarkerdata.visibility = GONE
        super.refreshContent(e, highlight)
    }


    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}