package com.dhamma.ignitedata.manager


import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.MArunningService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList

@Component
class RunningMAManger : BaseManager() {
    @Autowired
    lateinit var maManager: MAManager

    @Autowired
    lateinit var mArunningService: MArunningService

    //PARAMS
//    content.addProperty("id", "2")
//    content.addProperty("userid", "3")
//    content.addProperty("mode", "price")
//    content.addProperty("operator", "<")
//    content.addProperty("time", "200")
//    content.addProperty("percent", "0.3")
//    content.addProperty("sensitive", "3")
//    content.addProperty("days", "30")

    override fun runload(obj: JsonObject): List<HistoryIndicators> {
        //first time ma of (time) over number of days( days) , no of times ( sensitive)
        println("-------------RunningMAManger--------$obj-------------")
        var firsttimeMainDays = mArunningService::isRunningMA.curried()(obj.get("time").asInt)(obj.get("days").asInt)(obj.get("sensitive").asInt)


        var list = maManager.runload(obj);

        var result = list.parallelStream()
                .filter {
                    it.value > 0
                    // only want all MA > 0 , no -ve MA
                }
                .filter {
                    firsttimeMainDays(it.code)
                }
                .peek {
                    it.type = IndicatorType.MAF
                    it.message = "first time in MA ${obj.get("time")}"
                }.toList()

        return result
    }
}

//var hr = mamanager.runload(content)
//hr.forEach {
//
//    if (it.value > 0) {
//        //   println("-----$it-------${it.value}")
//        doLogic(it.code)
//
//    }
//
//}

