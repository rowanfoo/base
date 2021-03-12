package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.ChopRSIService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList


//Put a 14-period RSI on the same charts with the CHOP indicator on it.
// Draw a horizontal line at the 50 level of the RSI.
// When the CHOP is above 61.8 or in between the upper and lower limits, do nothing. When the CHOP is below 38.2, the lower threshold, take a trade according to the RSI.
// If the RSI is above 50 take a buy trade and in case the RSI is below 50 take a short sell trade. Keep a suitable stop loss or use some trailing stop method.

@Component
class ChopRSIManager : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var chopRSIService: ChopRSIService


    override fun runload(obj: JsonObject): List<HistoryIndicators> {
        println("-----********START****ChopRSIManager**********------$obj--------")

        //var time = obj.get("time").asString
        var typeid = obj.get("id").asLong
        var userid = obj.get("userid").asString

        var getDataz = ::getData.curried()(100)
        var getResult = chopRSIService::getResult.curried()(obj)

        return stocklist.parallelStream()
            //return stocklist
            .map(getDataz)
            .map(getResult)
            .filter { it.isPresent }
            .map {
                var value = it.get()

                var z = HistoryIndicators
                    .builder().code(value.get("code").asString)
                    .date(today())
                    .type(IndicatorType.CHOPRSI)
                    .value(0.0)
                    .type_id(typeid)
                    .userid(userid)
                    .message("CHOP ${value.get("chop").asDouble}:  RSI ${value.get("rsi").asString}   ")
                    .build()
                println("-------CHOP---------------$z")
                z
            }.toList()
    }
}
