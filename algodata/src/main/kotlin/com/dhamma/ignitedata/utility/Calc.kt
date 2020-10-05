package com.dhamma.ignitedata.utility

import com.dhamma.pesistence.entity.data.CoreData


class Calc {
//    fun mocingaverage(ma: Int, data: List<CoreData>, mode: String): Double {
//        var no = ma - 1
//        if (data.isEmpty()) {
//            print("------mocingaverage-----${data.size}-----------")
//        }
//
//
//        var all: List<Double> = data.map {
//            when (mode) {
//                "vol" -> it.volume.toDouble()
//                else -> it.close
//            }
//        }
//        return all.reduce { total, next -> total + next } / ma
//    }

    fun movingaverage(mode: String, data: List<CoreData>): Double {
        if (data.isEmpty()) {
            println("------mocingaverage-----${data.size}-----------")
        }


        var all: List<Double> = data.map {
            when (mode) {
                "vol" -> it.volume.toDouble()
                else -> it.close
            }
        }
//        var total = all.reduce { total, next -> total + next }
//        var mysize = total / 50
//        var eg = data.size - 1
//        var athtt = all.reduce { total, next -> total + next } / (data.size - 1)
        return all.reduce { total, next -> total + next } / (data.size - 1)
    }


    fun calculateRsi(closePrices: List<CoreData>): Double {
        var sumGain = 0.0
        var sumLoss = 0.0
//ERROR : ClosePrices[i-1] can be null if stock is suspended , close , sometime can be null
        for (i in 1 until closePrices.size) {
            // println("------calculateRsi-----${closePrices[i].close}-----------")

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
        var rsirs = 100 / (1 + relativeStrength)
        var rsi = 100 - rsirs
        return rsi
    }
}
