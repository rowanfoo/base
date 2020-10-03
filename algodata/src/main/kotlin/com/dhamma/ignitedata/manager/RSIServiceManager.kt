package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.concurrency.IgniteCacheConcurency
import com.dhamma.ignitedata.service.ShareService
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList

@Component
class RSIServiceManager : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    public fun add(data: JsonObject) {
        loadall(data)
    }


    override fun runload(obj: JsonObject) {

        var getDataz = ::getData.curried()(obj.get("time").asInt)
        var constructorz = ::constructor.curried()(::rsi)(::getRange)
//        stocklist.toObservable()
        stocklist.parallelStream()
                //  .observeOn(Schedulers.computation())
                .map(getDataz)
                .map(constructorz)
                .map {
                    HistoryIndicators
                            .builder().code(it.get("code").asString)
                            .date(today())
                            .type(IndicatorType.RSI)
                            .value(it.get("time").asDouble)
                            .message("RSI of ${it.get("time").asDouble} : price range of ${it.get("range").asString}   ")
                }.toList()

    }

    private fun constructor(fna: (List<CoreData>) -> Double, fnb: (List<CoreData>) -> String, series: List<CoreData>): JsonObject {
        var result = fna(series)
        var result1 = fnb(series)
        var content = JsonObject()
        content.addProperty("code", series[0].code)
        content.addProperty("time", result)
        content.addProperty("range", result1)

        return content
    }

}
