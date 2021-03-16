package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.springframework.stereotype.Component
import java.util.*


@Component
class VolumeService : BaseService() {
// want to compare today price to yesterday MA
    // so will select today price , then minus it from the index

    fun getResult(useralgo: JsonObject, data: List<CoreData>): Optional<JsonObject> {

        // println("------------DATA------${data.size}-----------")

//        var percent = useralgo.get("percent").asDouble
        var operator: String = useralgo.get("operator").asString
        //   println("------------getResult------$mode---------$percent------$operator--")

        useralgo.addProperty("percent", useralgo.get("volumex").asDouble)
// sometime  these vol , close can be null , will investigate why ??

        var today = data[0].volume.toDouble()
        var todayprice = data[0].close.toDouble()
        var yesterdayprice = data[1].close.toDouble()
        //var price  =      (true)?"BUY":"SELL"

        //     println("------------today------$today-----------")

        var datax = data.subList(1, data.size)

        var content = JsonObject()
//        println("------------CODE------${datax[0].code}-----------")

        content.addProperty("code", datax[0].code)
        content.addProperty("state", if (data[0].close > data[1].close) "BUY" else "SELL")

        content.addProperty("today", data[0].volAsString)

        var ma = Calc().movingaverage("vol", datax)

        // println("------------MA-----$ma-----------")

        content.addProperty("mavol", getMartketcapAsString(ma.toInt()))
//        content.addProperty("percentage", percent(today, ma))
        //println("------------CONTECT-----$content-----------")
        content.addProperty("percentage", percentformat(percent(today, ma)))
        return compare(useralgo, content)
    }
}
//  < 5%
//   < -20%


