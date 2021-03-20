package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.TA4J
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CovidService : BaseService() {
    //COVID - marker to compare ---- compare to the worst of time  COVID largest fall
    @Autowired
    lateinit var ta4j: TA4J

    fun getResult(code: String): Map<String, String> {
        return mapOf<String, String>(
            "code" to code,
            "lowestprice" to lowestprice(code).toString(),
            "lowestma100" to percentformat(lowestMA(code, 100) * 100),
            "lowestma200" to percentformat(lowestMA(code, 200) * 100)
        )
    }


    private fun lowestprice(code: String): Double {
        var z = coreDataIgniteService.datebtw(code, "2020-03-03", " 2020-04-30")
        var e = z.sortedBy { it.close }
        return e[0].close
    }

    fun lowestMA(code: String, ma: Int): Double {
        var z = coreDataIgniteService.datelseqlimit(code, "2020-04-30", "240")
        var s = ta4j.closePrice(z)

        var t = ta4j.sMAIndicator(z, ma)
        var x = t.timeSeries.endIndex
        var indexlowest = 0
        var lowest = 0.0
        for (i in 0..x) {
            var price = s.getValue(i).doubleValue()
            var maprice = t.getValue(i).doubleValue()
            var percent = (price - maprice) / maprice
            //  println("--$i----${t.timeSeries.getBar(i).dateName}------------********${price}}--------$maprice------------>>>$percent---------")
            if (percent < lowest) {
                lowest = percent
                indexlowest = i
            }
        }
        return lowest
    }


}
