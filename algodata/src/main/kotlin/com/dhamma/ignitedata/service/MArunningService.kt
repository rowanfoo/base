package com.dhamma.ignitedata.service

import com.dhamma.pesistence.entity.repo.DataRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.num.DoubleNum

@Component
class MArunningService {
    @Autowired
    lateinit var dataRepo: DataRepo


//    fun codeOffset(code: String, count: Int): SMAIndicator {
//
//        val d = dataRepo.findbyCodeOffsetBy(code, (count * 1.5) as Int)
//        d.reverse()
//        val series = BaseTimeSeries.SeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum::valueOf).build()
//        d.forEach { series.addBar(it.toBar()) }
//
//        val closePrice = ClosePriceIndicator(series)
//        val shortSma = SMAIndicator(closePrice, count)
//
//        return shortSma
//
//    }
//


    fun codeOffset(code: String, ma: Int, day: Int): Pair<ClosePriceIndicator, SMAIndicator> {

        val d = dataRepo.findbyCodeOffsetBy(code, (day) as Int)
        d.reverse()

        val series = BaseTimeSeries.SeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum::valueOf).build()
        d.forEach { series.addBar(it.toBar()) }

        val closePrice = ClosePriceIndicator(series)
        val shortSma = SMAIndicator(closePrice, ma)

        return Pair(closePrice, shortSma)

    }


    fun isRunningMA(ma: Int, days: Int, sensitive: Int, code: String): Boolean {
        var datapair = codeOffset(code, ma, (ma * 1.5).toInt() )

        var shortSma = datapair.second
        var closePrice = datapair.first
        val end: Int = shortSma.getTimeSeries().getEndIndex()
        //   shortSma.getTimeSeries().
        val above = ArrayList<Int>()
        for (i in end downTo end - days) {
            if (closePrice.getValue(i).doubleValue() > shortSma.getValue(i).doubleValue()) {
                above.add(i)
            }
        }

        return above.size < sensitive
//    if (above.size < sensitive) {
//        println("-------FOUND YOU ------$code--")
//    }

    }


//        fun twohundredma(code: String): SMAIndicator {
//
//        val d = dataRepo.findbyCodeOffsetBy(code, 300)
//        d.reverse()
//        // TimeSeries series = new BaseTimeSeries("my_live_series");
//        val series = BaseTimeSeries.SeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum::valueOf).build()
//        d.forEach { series.addBar(it.toBar()) }
//
//        val closePrice = ClosePriceIndicator(series)
//        val shortSma = SMAIndicator(closePrice, 200)
//
//        return shortSma
//        val start = shortSma.timeSeries.beginIndex
//        val end = shortSma.timeSeries.endIndex
//
//        var num = shortSma.getValue(start)
//        println("--------------$num")
//        println("--------------" + shortSma.timeSeries.getBar(start).dateName)
//
//
//        num = shortSma.getValue(end)
//        println("--------------$num")
//        println("--------------" + shortSma.timeSeries.getBar(end).dateName)
//
//        shortSma.timeSeries.

//    }


}
