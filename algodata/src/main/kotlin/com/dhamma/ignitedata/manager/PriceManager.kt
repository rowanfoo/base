package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.PriceService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PriceManager : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var priceService: PriceService


    public fun add(data: JsonObject) {
        loadall(data)
    }

    override fun runload(useralgo: JsonObject): List<HistoryIndicators> {
        var operator: String = useralgo.get("operator").asString
        var percent = useralgo.get("price").asString
        var typeid = useralgo.get("id").asLong

        var getResultLesser = coreDataIgniteService::lesserPercentlt.curried()(today().toString())
        var getResultGreater = coreDataIgniteService::lesserPercentlt.curried()(today().toString())

        var list = when {
            (operator == ">") -> getResultGreater(percent)
            (operator == "<") -> getResultLesser(percent)
            else -> listOf()
        }

        var msg = when {
            (operator == ">") -> "UP"
            (operator == "<") -> "FALL"
            else -> ""
        }

        return list
                .map {
                    var x = HistoryIndicators
                            .builder().code(it.code)
                            .date(it.date)
                            .type(if (operator == ">") IndicatorType.PRICE_UP else IndicatorType.PRICE_FALL)
                            .value(it.changepercent)
                            .type_id(typeid)
                            .message("$msg  4%   ${"%.3f".format((it.changepercent * 100))} % ").build()
                    println("----------runload---------${x}----")
                    x
                }.toList()


    }


//    override fun runload(obj: JsonObject) {
////add 1 because we want to compare today price  to  a 50d MA (incld yesterday but not today)
////        var stocklistx = listOf<String>("WPL.AX")
////        var stocklistx = listOf<String>("STO.AX")
//        var getResult = priceService::getResult.curried()(obj)
//
//        //stocklist
//
//        stocklist.parallelStream()
//                //  .observeOn(Schedulers.computation())
//                .map(::today)
//                .map(getResult)
//                .filter { it.isPresent }
//                .map {
//                    var value = it.get()
////                    println("----------runload---------${value.get("percentage").asDouble}----")
//                    //        println("----------runload---------${value}----")
//                    var x = HistoryIndicators
//                            .builder().code(value.get("code").asString)
//                            .date(today())
//                            .type(IndicatorType.MA)
//                            .value(value.get("percentage").asDouble)
//                            .message("fall > 4%   fall ${"%.3f".format((value["percentage"].asDouble * 100))} % ").build()
//                    println("----------runload---------${x}----")
//                    x
//                }.toList()
//
//    }


//    fun lesserPercentlt(date: String, fallpercent: String): List<CoreData> = ignitecache.values(" where  date=? and changepercent < ?", arrayOf(date, fallpercent))


}















