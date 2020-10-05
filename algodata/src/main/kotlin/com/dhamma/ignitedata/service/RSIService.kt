package com.dhamma.ignitedata.service

import arrow.syntax.function.curried
import com.dhamma.ignitedata.manager.getRange
import com.dhamma.ignitedata.manager.rsi
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.springframework.stereotype.Component
import java.util.*


@Component
class RSIService : BaseService() {


    fun getResult(useralgo: JsonObject, data: List<CoreData>): Optional<JsonObject> {


        var constructorz = ::constructor.curried()(::rsi)(::getRange)
        var jb = constructorz(data)
        var rsialgodata = useralgo.get("rsialgo").asDouble

//        return if (jb.get("rsi").asDouble != 0.0 && jb.get("rsi").asDouble < rsialgodata) Optional.of(jb)
//        else Optional.empty()

        var x = if (jb.get("rsi").asDouble != 0.0 && jb.get("rsi").asDouble < rsialgodata) Optional.of(jb)
        else Optional.empty()
        return x
    }


    private fun constructor(fna: (List<CoreData>) -> Double, fnb: (List<CoreData>) -> String, series: List<CoreData>): JsonObject {


        var result = fna(series)

        var result1 = fnb(series)

        var content = JsonObject()
        content.addProperty("code", series[0].code)
        content.addProperty("rsi", result)
        content.addProperty("range", result1)
        return content
    }

}
