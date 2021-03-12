package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.ChopMAService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList

/*
Feature this is to find where price have not gone anyway , consolidation phase flat MA , traders are all bored out and suddenly it break out . like OMH.AX
 */

@Component
class ChopMAManger : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var chopMAService: ChopMAService


    override fun runload(obj: JsonObject): List<HistoryIndicators> {
        println("-----********START****ChopMAManger**********------$obj--------")

        //var time = obj.get("time").asString
        var typeid = obj.get("id").asLong
        var userid = obj.get("userid").asString

        var getDataz = ::getData.curried()(100)
        var getResult = chopMAService::getResult.curried()(obj)

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
                    .type(IndicatorType.CHOPMA)
                    .value(0.0)
                    .type_id(typeid)
                    .userid(userid)
                    .message("CHOP MA of ${value.get("percent").asDouble} %: chop range of ${value.get("chop").asString}   ")
                    .build()
                println("-------CHOP---------------$z")
                z
            }.toList()
    }
}
