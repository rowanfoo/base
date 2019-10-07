package com.dhamma.algodata.algodata

import com.dhamma.algodata.utility.Calc
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MA {

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var ignite: Ignite


    fun process(a: JsonObject) {

        var no: Int = a.get("ma").asInt
        var mode: String = a.get("mode").asString

        println("--------load--------MA$no--")


        var cache = ignite.getOrCreateCache<String, Double>("MA$no:$mode")

        if (cache.size() == 0) {
            stocklist.parallelStream().forEach {
                // var series = ignitecache.get("$it")
                println("--------load----CODE-------------$it")
                var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(it, "$no"))

                println("--------load----SIZE-------------${series.size}")

                var num = Calc().mocingaverage(no, series, mode)

                println("--------AVG--------$it------------$num")
                cache.put("$it", num)

            }

        }


    }


}