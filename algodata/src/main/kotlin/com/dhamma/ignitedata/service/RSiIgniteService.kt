package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.CoreData
import org.springframework.stereotype.Component

@Component
class RSiIgniteService {
//    @Autowired
//    lateinit var stocklist: List<String>
//
//    @Autowired
//    lateinit var ignitecache: IgniteRepo<CoreData>
//
//    @Autowired
//    lateinit var ignite: Ignite
//
//    @Autowired
//    lateinit var cacheConcurency: IgniteCacheConcurency
//
//    fun getCache(data: JsonObject): IgniteCache<String, Pair<Double, String>> {
//
//        var key = "${data.get("rsi")}-${data.get("offset")}"
//
//        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$key")
//        if (cache.size() == 0) {
//            loadall(data)
//        }
//        return cache
//    }

//    fun process(a: JsonObject) {
//        //      var from: Int = a.get("from").asInt
//        //   var to: Int = a.get("to").asInt
//        var time: Int = a.get("time").asInt
//        var code: String = a.get("code").asString
//        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$time")
//        var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(code, "$time"))
//        var num = Calc().calculateRsi(series.reversed())
//
//        var max = series.maxBy { it.close }!!.close
//        var min = series.minBy { it.close }!!.close
//        var percent = String.format("%.1f", (((max - min) / max) * 100))
//
//        cache.put("$code", Pair(String.format("%.1f", num).toDouble(), "$max - $min   $percent"))
//    }


//    fun process(series: List<CoreData>): Double {
//        var num = Calc().calculateRsi(series.reversed())
//        return String.format("%.1f", num).toDouble()
//    }
//
//    private fun getRange(series: List<CoreData>): String {
//        var max = series.maxBy { it.close }!!.close
//        var min = series.minBy { it.close }!!.close
//        var percent = String.format("%.1f", (((max - min) / max) * 100))
//        return "$max - $min   $percent"
//    }
//
//    private fun runload2(obj: JsonObject) {
//        var time: Int = obj.get("rsi").asInt
//        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$time")
//
//        stocklist.parallelStream().forEach {
//            var time: Int = obj.get("rsi").asInt
//            var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$time")
//            var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(it, "$time"))
//            cache.put("$it", Pair(process(series), getRange(series)))
//        }
//    }


//    public fun runload(obj: JsonObject) {
////        stocklist.parallelStream().forEach {
////            var content = JsonObject()
////            content.addProperty("code", it)
////            content.addProperty("time", obj.get("rsi").asInt)
////            process(content)
////        }
//
//        var getDataz = ::getData.curried()(obj.get("time").asInt)(obj.get("offset").asInt)
//        var constructorz = ::constructor.curried()(::rsi)(::getRange)
//        stocklist.toObservable()
//                .observeOn(Schedulers.computation())
//                .map(getDataz)
//                .map(constructorz)
//                .subscribe {
//                    println("Received RESULT $it")
//                    var key = getKey(obj)
//                    var cache = ignite.getOrCreateCache<String, Pair<Double, String>>(key)
//                    cache.put(it.get("code").asString, Pair(it.get("rsi").asDouble, it.get("range").asString))
//                }


//        stocklist.parallelStream()
//                .map(getDataz)
//                //        .observeOn(Schedulers.computation())
//                .map(constructorz)
//
//                .forEach {
//                    println("Received $it")
//                    var key = getKey(obj)
//                    var cache = ignite.getOrCreateCache<String, Pair<Double, String>>(key)
//                    cache.put(it.get("code").asString, Pair(it.get("rsi").asDouble, it.get("range").asString))
//                }


//    }

    fun rsi(series: List<CoreData>): Double {
//    fun processA(series: List<CoreData>): Pair<String, Double> {
//        var time: Int = a.get("time").asInt
        //       var code: String = a.get("code").asString
//        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>("RSI$time")
//        var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(code, "$time"))

        var num = Calc().calculateRsi(series.reversed())

//        var max = series.maxBy { it.close }!!.close
//        var min = series.minBy { it.close }!!.close
//        var percent = String.format("%.1f", (((max - min) / max) * 100))


//        return Pair<String, Double>(series[0].code, String.format("%.1f", num).toDouble())

        return String.format("%.1f", num).toDouble()
//        cache.put("$code", Pair(String.format("%.1f", num).toDouble(), "$max - $min   $percent"))
    }


//    private fun getData(time: Int, offset: Int, code: String): List<CoreData> {
////        var code: String = data.get("code").asString
////        var time: Int = data.get("time").asInt
////        var offset: Int = data.get("offset").asInt
//        var z = ignitecache.values(" where code=?  order by date desc  LIMIT ?  OFFSET ? ", arrayOf(code, "$time", "$offset"))
//
//        // println("Received $z")
//
//        return z
//
//    }

    //    private fun getData(time: Int, offset: Int, code: String)=ignitecache.values(" where code=?  order by date desc  LIMIT ?  OFFSET ? ", arrayOf(code, "$time", "$offset"))
//    private fun getData(time: Int, offset: Int, code: String): List<CoreData> {
//
////        var code: String = data.get("code").asString
////        var time: Int = data.get("time").asInt
////        var offset: Int = data.get("offset").asInt
//        println("Received $code ----------------${Thread.currentThread().name}")
//        var z = ignitecache.values(" where code=?  order by date desc  LIMIT ?  OFFSET ? ", arrayOf(code, "$time", "$offset"))
//
////        println("Received $z ---------------------------------${Thread.currentThread().name}")
////
//        return z
////
//    }

//    private fun getData(data: JsonObject): List<CoreData> {
//        var code: String = data.get("code").asString
//        var time: Int = data.get("time").asInt
//        var offset: Int = data.get("offset").asInt
//        return ignitecache.values(" where code=?  order by date desc  LIMIT ?  OFFSET ? ", arrayOf(code, "$time", "$offset"))
//    }


//    fun loadall(obj: JsonObject) {
//        println("--------LOAD ALL  RSI----------")
////        var time = obj.get("rsi").asInt
//        //      cacheConcurency.process("RSI$time", obj, ::runload)
//        cacheConcurency.process(getKey(obj), obj, ::runload)
//    }


//    private fun getKey(data: JsonObject) = "RSI${data.get("rsi")}-${data.get("offset")}"
//
//    private fun constructor(fna: (List<CoreData>) -> Double, fnb: (List<CoreData>) -> String, series: List<CoreData>): JsonObject {
//        var result = fna(series)
//        var result1 = fnb(series)
//        var content = JsonObject()
//        content.addProperty("code", series[0].code)
//        content.addProperty("rsi", result)
//        content.addProperty("range", result1)
//
//        return content
//    }


}
