package com.dhamma.ignitedata.utility

import com.dhamma.pesistence.entity.data.CoreData
import org.springframework.stereotype.Component
import org.ta4j.core.BaseTimeSeries
import org.ta4j.core.TimeSeries
import org.ta4j.core.indicators.ChopIndicator
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


    private fun createSeries(data: List<CoreData>): TimeSeries {
        var t = data.reversed()
        val series = BaseTimeSeries.SeriesBuilder().withName("mySeries").withNumTypeOf(DoubleNum::valueOf).build()
        t.forEach {
            series.addBar(it.toBar())
        }
        return series
    }

}
