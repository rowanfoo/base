package com.dhamma.service.utility

import com.dhamma.pesistence.entity.data.CoreData


class Calc {
//    fun mocingaverage(ma: Int, data: List<Double>): Double {
//        var no = ma - 1
//        var all: List<Double> = data.slice(0..no)
//        return all.reduce { total, next -> total + next } / all.count()
//    }
//    fun mocingaverage(ma: Int, series: List<CoreDataService>): Double {
//        var no = ma - 1
//
//        println("--------mocingaverage----${series.size}-----")
//        var prices = series.slice(series.size -  ma..series.size)
//
////        prices.map {it.close }
//        return prices.map { it.close }.reduce { total, next -> total + next } / prices.count()
//    }
//

    fun mocingaverage(ma: Int, data: List<CoreData>, mode: String): Double {
        var no = ma - 1

        //     var from = data.size - ma + 1
        //   var till = data.count() - 1
        //     println("------ma---${data[0]}---:::--------${data[data.count() - 1]}-------")

        //var all: List<Double> = data.slice(from..till).map {
        var all: List<Double> = data.map {
            when (mode) {
                "vol" -> it.volume.toDouble()
                else -> it.close
            }
        }
        return all.reduce { total, next -> total + next } / ma

        //   println("------ma---$a-------")
        //  return a

    }


    fun calculateRsi(closePrices: List<CoreData>): Double {

        var sumGain = 0.0
        var sumLoss = 0.0
//        println("------ma---${closePrices[0]}---:::--------${closePrices[closePrices.count() - 1]}-------")
        println("-*********RSI****************${closePrices.size}-------")
        for (i in 1 until closePrices.size) {
            println("------ma---${closePrices[i].close}---:::--------${closePrices[i - 1].close}-------")

            var difference = closePrices[i].close - closePrices[i - 1].close

            if (difference >= 0) {
//                println("------rsi gain---${sumGain}-------")
                sumGain += difference;
            } else {
                sumLoss -= difference;
            }

        }

        if (sumGain == 0.0) return 0.0

//        var relativeStrength = sumGain / sumLoss;
//
//        return 100.0 - (100.0 / (1 + relativeStrength))

//        println("--TOTAL----rsi gain---${sumGain}---:::----losses----${sumLoss}-------")

        var rsitime = closePrices.size - 1

        var relativeStrength = (sumGain / rsitime) / (sumLoss / rsitime)
        println("-----relativeStrength---${relativeStrength}-------")
        var rsirs = 100 / (1 + relativeStrength)
        var rsi = 100 - rsirs

        println("-*********RSI******${closePrices[1].code}**********${rsi}-------")
        return rsi
    }


}