package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.MFIIndicatorService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MFIManger : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var mfiIndicatorService: MFIIndicatorService

    public fun add(data: JsonObject) {
        loadall(data)
    }


    override fun runload(obj: JsonObject): List<HistoryIndicators> {
        var time = obj.get("time").asInt
        var typeid = obj.get("id").asLong
        var userid = obj.get("userid").asString

        var getDataz = ::getData.curried()(360)
        var getResult = mfiIndicatorService::getResult.curried()(obj)

        //return stocklist.parallelStream()
        return stocklist
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
                    .type(IndicatorType.MFI)
                    .value(value.get("MFI").asDouble)
                    .type_id(typeid)
                    .userid(userid)
                    .message("MFI of ${value.get("MFI").asString} : with RSI of  ${value.get("RSI").asString}   ")
                    .build()
                println("-------MFI---------------$z")
                z
            }.toList()
    }
}
