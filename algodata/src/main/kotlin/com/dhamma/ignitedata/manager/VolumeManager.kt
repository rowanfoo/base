package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.VolumeService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList

@Component
class VolumeManager : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var volumeService: VolumeService


    public fun add(data: JsonObject) {
        loadall(data)
    }


    override fun runload(obj: JsonObject): List<HistoryIndicators> {

        println("----------runload---CONDIFG------${obj}----")
//add 1 because we want to compare today price  to  a 50d MA (incld yesterday but not today)
//        var stocklistx = listOf<String>("WPL.AX")
//        var stocklistx = listOf<String>("STO.AX")
        var time = obj.get("time").asInt
        var typeid = obj.get("id").asLong
        var userid= obj.get("userid").asString
        var getDataz = ::getData.curried()(time + 1)
        var getResult = volumeService::getResult.curried()(obj)

        //stocklist

        return stocklist.parallelStream()
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
                            .type(IndicatorType.VOLUME)
                            .value(value.get("percentage").asDouble)
                            .type_id(typeid)
                            .userid(userid)
                            .message("${value["state"].asString}  --today  ${value["today"].asString}-------vs ma $ ${value["mavol"].asString}----:$ ${value["percentage"].asString}%)").build()
                    println("----------runload---------${x}----")
                    x
                }.toList()
    }

}



