package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.RSIService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList

@Component
class RSIManager : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var rSIService: RSIService


    public fun add(data: JsonObject) {
        loadall(data)
    }


    override fun runload(obj: JsonObject): List<HistoryIndicators> {
        var time = obj.get("time").asInt
        var typeid = obj.get("id").asLong
        var getDataz = ::getData.curried()(time)
        var getResult = rSIService::getResult.curried()(obj)

        return stocklist.parallelStream()
                // return stocklist
                //  .observeOn(Schedulers.computation())
                .map(getDataz)
                .map(getResult)
                .filter { it.isPresent }
                .map {
                    var value = it.get()
                    //println("-------RSI---------------$value")
                    var z = HistoryIndicators
                            .builder().code(value.get("code").asString)
                            .date(today())
                            .type(IndicatorType.RSI)
                            .value(value.get("rsi").asDouble)
                            .type_id(typeid)
                            .message("RSI of ${value.get("rsi").asDouble} : price range of ${value.get("range").asString}   ")
                            .build()
                    // println("-------RSI---------------$z")
                    z
                }.toList()
    }

}

