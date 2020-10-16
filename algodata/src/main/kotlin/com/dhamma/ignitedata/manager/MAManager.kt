package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.MAService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList

@Component
class MAManager : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var maService: MAService


//    public fun add(data: JsonObject) {
//        loadall(data)
//    }


    fun code(code: String, date: String, time: Int, mode: String): JsonObject {
//add 1 because we want to compare today price  to  a 50d MA (incld yesterday but not today)
        var data = coreDataIgniteService.datelseqlimit(code, date, (time + 1).toString())
        var content = JsonObject()
        content.addProperty("mode", mode)
        return maService.getMA(content, data)
    }

    override fun runload(obj: JsonObject): List<HistoryIndicators> {
//add 1 because we want to compare today price  to  a 50d MA (incld yesterday but not today)
//        var stocklistx = listOf<String>("WPL.AX")
//        var stocklistx = listOf<String>("STO.AX")
        println("-----********START****runload**********------$obj--------")
        var time = obj.get("time").asString
        var typeid = obj.get("id").asLong
        var userid = obj.get("userid").asString

        var getDataz = ::getData.curried()(obj.get("time").asInt + 1)
        var getResult = maService::getResult.curried()(obj)

        //stocklist

        var list = stocklist.parallelStream()
                //  .observeOn(Schedulers.computation())
                .map(getDataz)
                .map(getResult)
                .filter { it.isPresent }
                .map {
                    var value = it.get()
//                    println("----------runload---------${value.get("percentage").asDouble}----")
                    //        println("----------runload---------${value}----")
                    var x = HistoryIndicators
                            .builder().code(value.get("code").asString)
                            .date(today())
                            .type(IndicatorType.MA)
                            .value(value.get("percentage").asDouble)
                            .type_id(typeid)
                            .userid(userid)
                            .message("today  ${value["today"].asString}-------vs ma $ ${value["maprice"].asString}----:$ ${value["percentage"].asString}%)").build()
                    println("----------runload---------${x}----")
                    x
                }.toList()
        return list
    }

}
