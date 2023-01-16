package com.systudio.datacovid19.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat


class MyAxisValueFormatter : ValueFormatter() {
    var format : DecimalFormat = DecimalFormat()

    fun MyAxisValueFormatter(){
        format = DecimalFormat("###,###,###,##0.0")
    }

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return format.format(value)+" $ "
    }


}