package com.dhamma.ignitedata.service

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.concurrency.IgniteCacheConcurency
import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RSiIgniteService {
    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var ignite: Ignite

    @Autowired
    lateinit var cacheConcurency: IgniteCacheConcurency

    fun getCache(data: JsonObject): IgniteCache<String, Pair<Double, String>> {
        var rsidata = data.get("rsi").asInt

        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$rsidata")
        if (cache.size() == 0) {
            loadall(data)
        }
        return cache
    }

    fun process(a: JsonObject) {
        //      var from: Int = a.get("from").asInt
        //   var to: Int = a.get("to").asInt
        var time: Int = a.get("time").asInt
        var code: String = a.get("code").asString
        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$time")

        var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(code, "$time"))

        var num = Calc().calculateRsi(series.reversed())
        var max = series.maxBy { it.close }!!.close
        var min = series.minBy { it.close }!!.close
        var percent = String.format("%.1f", (((max - min) / max) * 100))

        cache.put("$code", Pair(String.format("%.1f", num).toDouble(), "$max - $min   $percent"))
    }

    private fun runload(obj: JsonObject) {
        stocklist.parallelStream().forEach {
            var content = JsonObject()
            content.addProperty("code", it)
            content.addProperty("time", obj.get("rsi").asInt)
            process(content)
        }
    }

    fun loadall(obj: JsonObject) {
        println("--------LOAD ALL  RSI----------")
        var time = obj.get("rsi").asInt
        cacheConcurency.process("RSI$time", obj, ::runload)
    }
}