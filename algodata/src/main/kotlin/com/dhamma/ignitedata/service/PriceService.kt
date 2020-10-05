package com.dhamma.ignitedata.service

import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.springframework.stereotype.Component
import java.util.*


@Component
class PriceService : BaseService() {

    fun getResult(useralgo: JsonObject, data: CoreData): Optional<JsonObject> {

        var operator: String = useralgo.get("operator").asString

        //var percent = useralgo.get("price").asString

        useralgo.addProperty("percent", useralgo.get("price").asString)

        var today = data.close

        var content = JsonObject()
        content.addProperty("code", data.code)
        content.addProperty("today", today)
//        content.addProperty("percentage", percent(today, ma))
        //println("------------CONTECT-----$content-----------")
//        content.addProperty("percent", percent)
        content.addProperty("percentage", data.changepercent)


        // println("------------CONTECT------${content}-----------")
        //     println("------------MA------${content.get("maprice").asDouble}-------vs TODARY  ${today}----")

        //   println("------------PERCENTAGE -----${content.get("percentage").asDouble}-----------")
//        when {
//            (operator == ">") -> {
//                if (content.get("percentage").asDouble > percent) {
//                    return Optional.of(content)
//                }
//            }
//            //find all  < MA
//            (operator == "<") -> {
////                if ( percent > 0  ){
//
//                if (content.get("percentage").asDouble < percent) {
//                    //      println("------------>>>>>>-----${content.get("percentage").asDouble}-----${percent}------")
//                    return Optional.of(content)
//                }
////                }
//                //find all  < MA -20
//
////                if ( percent < 0  ){
////
////                    if (content.get("percentage").asDouble < percent) {
////                        println("------------MA-----${content.get("percentage").asDouble}-----${percent}------")
////                        return Optional.of(content)
////                    }
////                }
//
//
//            }
//            else -> {
//                if (content.get("percentage").asDouble == percent) {
//                    return Optional.of(content)
//                }
//            }
//        }
//        return Optional.empty()
        return compare(useralgo, content)
    }

}
//  < 5%
//   < -20%
