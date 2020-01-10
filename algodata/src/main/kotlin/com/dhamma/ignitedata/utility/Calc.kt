package com.dhamma.ignitedata.utility

import com.dhamma.pesistence.entity.data.CoreData


class Calc {
    fun mocingaverage(ma: Int, data: List<CoreData>, mode: String): Double {
        var no = ma - 1
        var all: List<Double> = data.map {
            when (mode) {
                "vol" -> it.volume.toDouble()
                else -> it.close
            }
        }
        return all.reduce { total, next -> total + next } / ma
    }

    fun calculateRsi(closePrices: List<CoreData>): Double {

        var sumGain = 0.0
        var sumLoss = 0.0
        for (i in 1 until closePrices.size) {
            var difference = closePrices[i].close - closePrices[i - 1].close

            if (difference >= 0) {
                sumGain += difference;
            } else {
                sumLoss -= difference;
            }
        }
        if (sumGain == 0.0) return 0.0

        var rsitime = closePrices.size - 1
        var relativeStrength = (sumGain / rsitime) / (sumLoss / rsitime)
        println("-----relativeStrength---${relativeStrength}-------")
        var rsirs = 100 / (1 + relativeStrength)
        var rsi = 100 - rsirs
        return rsi
    }
}