package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.concurrency.IgniteCacheConcurency
import com.dhamma.ignitedata.service.RSiIgniteService
import com.dhamma.ignitedata.service.ShareService
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import io.reactivex.rxjava3.kotlin.toObservable
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RSIServiceManager {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var ignite: Ignite

    @Autowired
    lateinit var cacheConcurency: IgniteCacheConcurency

    @Autowired
    lateinit var rsiIgniteService: RSiIgniteService

    @Autowired
    lateinit var shareService: ShareService


    public fun getCache(data: JsonObject): IgniteCache<String, Pair<Double, String>> {

//        var key = "${data.get("rsi")}-${data.get("offset")}"
//        println("------------RSIServiceManager--------------------getCache-------${data}--")
        var key = "${data.get("time")}-${data.get("offset")}"
        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$key")
        if (cache.size() == 0) {
            loadall(data)
        }
        return cache
    }

    public fun runload(obj: JsonObject) {
//        println("------------RSIServiceManager--------------------runload-------$obj--")
//        println("------------RSIServiceManager--------------------runload-------${obj.get("time")}--")

        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>(getKey(obj))

        var getDataz = ::getData.curried()(obj.get("time").asInt)(obj.get("offset").asInt)
        var constructorz = ::constructor.curried()(rsiIgniteService::rsi)(shareService::getRange)
        stocklist.toObservable()
                //  .observeOn(Schedulers.computation())
                .map(getDataz)
                .map(constructorz)
                .subscribe {
                    //  println("Received RESULT $it")

                    // cache.put(it.get("code").asString, Pair(it.get("rsi").asDouble, it.get("range").asString))
                    cache.put(it.get("code").asString, Pair(it.get("time").asDouble, it.get("range").asString))
                }
    }


    private fun getData(time: Int, offset: Int, code: String): List<CoreData> {
        //  println("Received $code ----------------${Thread.currentThread().name}")
        print("------getData-----${code}-----------")
        var z = ignitecache.values(" where code=?  order by date desc  LIMIT ?  OFFSET ? ", arrayOf(code, "$time", "$offset"))
        return z
    }


//    fun runload(obj: JsonObject) {
//        var volumema = obj.get("volumema").asInt
//
//        println("-------VolumeMA------$volumema-----")
//
//        var cache = ignite.getOrCreateCache<String, Double>("MA$volumema:vol")
//
//        if (cache.size() == 0) {
//
//
//            stocklist.parallelStream().forEach {
//                // var series = ignitecache.get("$it")
//                println("--------load----CODE-------------$it")
//                var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(it, "$volumema"))
//                var num = Calc().mocingaverage(volumema, series, "vol")
//                cache.put("$it", num)
//
//            }
//
//
//        }
//    }


    fun loadall(obj: JsonObject) {
        println("--------LOAD ALL  RSI----------")
        println("------------RSIServiceManager---------------------LOAD ALL -------${obj}--")
        cacheConcurency.process(getKey(obj), obj, ::runload)
    }


    //    private fun getKey(data: JsonObject) = "RSI${data.get("rsi")}-${data.get("offset")}"
    private fun getKey(data: JsonObject) = "RSI${data.get("time")}-${data.get("offset")}"

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
