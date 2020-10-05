package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.springframework.stereotype.Component
import java.util.*


@Component
class MAService : BaseService() {
// want to compare today price to yesterday MA
    // so will select today price , then minus it from the index

    fun getResult(useralgo: JsonObject, data: List<CoreData>): Optional<JsonObject> {

        // println("------------DATA------${data.size}-----------")

        var mode: String = useralgo.get("mode").asString
        var percent = useralgo.get("percent").asDouble
        var operator: String = useralgo.get("operator").asString

        //   println("------------getResult------$mode---------$percent------$operator--")

        var today = data[0].close


        //     println("------------today------$today-----------")

        var datax = data.subList(1, data.size)

        var content = JsonObject()
//        println("------------CODE------${datax[0].code}-----------")

        content.addProperty("code", datax[0].code)

        content.addProperty("today", today)

        var ma = Calc().movingaverage(mode, datax)

        // println("------------MA-----$ma-----------")

        content.addProperty("maprice", ma)
//        content.addProperty("percentage", percent(today, ma))
        //println("------------CONTECT-----$content-----------")
        content.addProperty("percentage", percent(today, ma))
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