package com.dhamma.service.algodata

import com.dhamma.service.utility.Calc
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.concurrency.IgniteCacheConcurency
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class VolumeMA {

    @Autowired
    lateinit var stocklist: List<String>
    @Autowired
    lateinit var ignite: Ignite
    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var cacheConcurency: IgniteCacheConcurency

    fun getCache(data: JsonObject): IgniteCache<String, Double> {
        var volumema = data.get("volumema").asInt

        var cache = ignite.getOrCreateCache<String, Double>("MA$volumema:vol")
        if (cache.size() == 0) {
            process(data)
        }
        return cache
    }

    fun process(data: JsonObject) {
        var volumema = data.get("volumema").asInt

        println("-------VolumeMA------$volumema-----")

        var cache = ignite.getOrCreateCache<String, Double>("MA$volumema:vol")

        if (cache.size() == 0) {


            stocklist.forEach {
                // var series = ignitecache.get("$it")
                println("--------load----CODE-------------$it")
                var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(it, "$volumema"))

                println("--------load----SIZE-------------${series.size}")

                var num = Calc().mocingaverage(volumema, series, "vol")

                println("--------AVG--------$it------------$num")
                cache.put("$it", num)

            }


        }


    }


    fun runload(obj: JsonObject) {
        var volumema = obj.get("volumema").asInt

        println("-------VolumeMA------$volumema-----")

        var cache = ignite.getOrCreateCache<String, Double>("MA$volumema:vol")

        if (cache.size() == 0) {


            stocklist.parallelStream().forEach {
                // var series = ignitecache.get("$it")
                println("--------load----CODE-------------$it")
                var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(it, "$volumema"))

                println("--------load----SIZE-------------${series.size}")

                var num = Calc().mocingaverage(volumema, series, "vol")

                println("--------AVG--------$it------------$num")
                cache.put("$it", num)

            }


        }


        println("--------DONE ALL  VOLUMEA----------")

    }

    fun loadall(obj: JsonObject) {
        println("--------LOAD ALL  VOLUMEAVG----------")
        var volumema = obj.get("volumema").asInt

        println("-------VolumeMA------$volumema-----")

        cacheConcurency.process("MA$volumema:vol", obj, ::runload)


    }


}