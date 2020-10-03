package com.dhamma.ignitedata.service

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
abstract class MaIgniteService {

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var ignite: Ignite


    fun getCache(a: JsonObject): IgniteCache<String, Double> {
        var no: Int = a.get("ma").asInt
        var mode: String = a.get("mode").asString

        var cache = ignite.getOrCreateCache<String, Double>("MA$no:$mode")
        if (cache.size() == 0) {
            process(a)
            cache = ignite.getOrCreateCache<String, Double>("MA$no:$mode")
        }
        return cache
    }


    fun getCode(a: JsonObject): Double {
        var no: Int = a.get("ma").asInt
        var mode: String = a.get("mode").asString
        var code: String = a.get("code").asString
        var cache = ignite.getOrCreateCache<String, Double>("MA$no:$mode")
        if (cache.size() == 0) {
            process(a)
            var newcache = ignite.getOrCreateCache<String, Double>("MA$no:$mode")

            return newcache.get(code)
        }
        return cache.get(code)
    }

    fun process(a: JsonObject) {

        var no: Int = a.get("ma").asInt
        var mode: String = a.get("mode").asString
        var cache = ignite.getOrCreateCache<String, Double>("MA$no:$mode")

        if (cache.size() == 0) {
            stocklist.parallelStream().forEach {
                var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(it, "$no"))
                var num = Calc().movingaverage(series, mode)
                cache.put("$it", num)
            }

        }
    }






}

