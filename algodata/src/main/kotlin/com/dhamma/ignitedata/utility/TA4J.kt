package com.dhamma.ignitedata.utility

import com.dhamma.ignitedata.service.percentformat
import com.dhamma.pesistence.entity.data.CoreData
import org.springframework.stereotype.Component
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.ChopIndicator
import org.ta4j.core.indicators.RSIIndicator
import org.ta4j.core.indicators.SMAIndicator
import org.ta4j.core.indicators.helpers.ClosePriceIndicator
import org.ta4j.core.num.DoubleNum

@Component
class TA4J {


    fun closePrice(data: List<CoreData>): ClosePriceIndicator {
//        d.reverse()
//        val series = BaseTimeSeries.SeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum::valueOf).build()
//        d.forEach {
//            series.addBar(it.toBar())
//        }
        val closePrice = ClosePriceIndicator(createSeries(data))
        return closePrice
    }

//    fun closeDateBetweenPrice(code: String, date1: String, date2: String): ClosePriceIndicator {
//        val d: List<CoreData> = dataRepo.findAll(
//            QCoreData.coreData.code.eq(code).and(
//                QCoreData.coreData.date.between(LocalDate.parse(date1), LocalDate.parse(date2))
//
//            )
//        ).toList()
////        var t = d.reversed()
////        val series = BaseTimeSeries.SeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum::valueOf).build()
////        d.forEach {
////            series.addBar(it.toBar())
////
////        }
//
//        val closePrice = ClosePriceIndicator(createSeries(d))
//        return closePrice
//    }

    fun chopIndicator(data: List<CoreData>): ChopIndicator {
        return ChopIndicator(closePrice(data).timeSeries, 14, 100)
    }

    fun sMAIndicator(data: List<CoreData>, ma: Int): SMAIndicator {
        return SMAIndicator(closePrice(data), ma)
    }

//    fun sMAIndicatorAbovePercent(code: String, ma_param: Int, counter: Double, sensitive: Double) {

    fun sMAIndicatorAbovePercent(data: List<CoreData>, ma_param: Int, counter: Double, sensitive: Double): String {
//        var sensitive = 0.03;
        var ma = SMAIndicator(closePrice(data), ma_param)
        val closePrice = ClosePriceIndicator(createSeries(data))

        var x = closePrice.timeSeries.endIndex
        var y = closePrice.timeSeries.endIndex - counter.toInt()

        var count = 0
        for (i in x downTo y) {
            var price = closePrice.getValue(i).doubleValue()
            var maprice = ma.getValue(i).doubleValue()
            var percent = (price - maprice) / maprice
            if (percent > sensitive) {
                count++
            }
        }
        return percentformat(((count.toDouble() / counter) * 100).toDouble())
    }


    fun rSIIndicator(data: List<CoreData>): RSIIndicator {
        return RSIIndicator(closePrice(data), 14)
    }


    private fun createSeries(data: List<CoreData>): TimeSeries {
        var t = data.reversed()
        val series = BaseTimeSeries.SeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum::valueOf).build()
        t.forEach {
            series.addBar(it.toBar())
        }
        return series
    }

}
