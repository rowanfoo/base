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


    fun codeOffset(code: String, count: Int): SMAIndicator {

        val d = dataRepo.findbyCodeOffsetBy(code, (count * 1.5) as Int)
        d.reverse()
        val series = BaseTimeSeries.SeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum::valueOf).build()
        d.forEach { series.addBar(it.toBar()) }

        val closePrice = ClosePriceIndicator(series)
        val shortSma = SMAIndicator(closePrice, count)

        return shortSma

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