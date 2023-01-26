package com.systudio.datacovid19.utils

import java.text.DecimalFormat

class FormatNumber() {
     fun formatNumber(number: Int): String {
        if (number >= 1000000){
            val formatter = DecimalFormat("#,###.#")
            return formatter.format(number / 1000.0) + "K"
        } else {
            val formatter = DecimalFormat("#,###.#")
            return formatter.format(number / 1000.0) +"K"
        }

    }
}