package com.dhamma.ignitedata.service

import org.springframework.stereotype.Component

@Component
class VolumeMaIgniteService {

//    @Autowired
//    lateinit var stocklist: List<String>
//
//    @Autowired
//    lateinit var ignite: Ignite
//
//    @Autowired
//    lateinit var ignitecache: IgniteRepo<CoreData>
//
//    @Autowired
//    lateinit var cacheConcurency: IgniteCacheConcurency
//
//    fun getCache(data: JsonObject): IgniteCache<String, Double> {
//        var volumema = data.get("volumema").asInt
//
//        var cache = ignite.getOrCreateCache<String, Double>("MA$volumema:vol")
//        if (cache.size() == 0) {
//            process(data)
//        }
//        return cache
//    }
//
//    fun process(data: JsonObject) {
//        var volumema = data.get("volumema").asInt
//        var cache = ignite.getOrCreateCache<String, Double>("MA$volumema:vol")
//
//        if (cache.size() == 0) {
//            stocklist.forEach {
//                var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(it, "$volumema"))
//                //   var num = Calc().mocingaverage(volumema, series, "vol")
//                // cache.put("$it", num)
//
//            }
//        }
//    }
//
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
//                //   var num = Calc().mocingaverage(volumema, series, "vol")
//                //    cache.put("$it", num)
//
//            }
//
//
//        }
//    }
//
//
//    fun movingavergae(series: List<CoreData>): Double {
//        var num = Calc().movingaverage(series, "vol")
//        return String.format("%.1f", num).toDouble()
//    }
//
//
//    fun loadall(obj: JsonObject) {
//        var volumema = obj.get("volumema").asInt
//        cacheConcurency.process("MA$volumema:vol", obj, ::runload)
//
//
//    }

}
