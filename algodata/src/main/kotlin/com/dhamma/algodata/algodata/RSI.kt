package com.dhamma.algodata.algodata

import com.dhamma.algodata.utility.Calc
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.concurrency.IgniteCacheConcurency
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

//import com.dhamma.concurrency

@Component
class RSI {
    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var ignite: Ignite

    @Autowired
    lateinit var cacheConcurency: IgniteCacheConcurency


    fun process(a: JsonObject) {
        //      var from: Int = a.get("from").asInt
        //   var to: Int = a.get("to").asInt
        var time: Int = a.get("time").asInt
        var code: String = a.get("code").asString
        println("--------WILL PROCESS ----------")


        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$time")

        var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(code, "$time"))
        println("--------load----SIZE-------------${series.size}")

        var num = Calc().calculateRsi(series.reversed())
        var max = series.maxBy { it.close }!!.close
        var min = series.minBy { it.close }!!.close
        var percent = String.format("%.1f", (((max - min) / max) * 100))

        println("--------AVG--------$code------------$num")
        cache.put("$code", Pair(String.format("%.1f", num).toDouble(), "$max - $min   $percent"))


    }

    private fun runload(obj: JsonObject) {
        println("--------RUNLOAD   RSI----------")
        stocklist.parallelStream().forEach {
            var content = JsonObject()
            content.addProperty("code", it)
            content.addProperty("time", obj.get("rsi").asInt)
            process(content)
        }
        println("--------DONE ALL  RSI----------")

    }

    fun loadall(obj: JsonObject) {
        println("--------LOAD ALL  RSI----------")
        var time = obj.get("rsi").asInt
        cacheConcurency.process("RSI$time", obj, ::runload)
    }
}