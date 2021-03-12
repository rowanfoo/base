package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.CoreData


fun rsi(series: List<CoreData>): Double {
    var num = Calc().calculateRsi(series.reversed())
    return String.format("%.1f", num).toDouble()
}

fun getRange(series: List<CoreData>): String {
    var max = series.maxBy { it.close }!!.close
    var min = series.minBy { it.close }!!.close
    var percent = String.format("%.1f", (((max - min) / max) * 100))
    return "$max - $min   $percent"
}

fun percent(currentprrice: Double, vsprrice: Double) =
    String.format("%.3f", (currentprrice - vsprrice) / vsprrice).toDouble()

fun percentformat(d: Double) = String.format("%.2f", d)


