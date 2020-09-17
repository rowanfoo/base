package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.concurrency.IgniteCacheConcurency
import com.dhamma.ignitedata.service.VolumeMaIgniteService
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import io.reactivex.rxjava3.kotlin.toObservable
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class VolumeServiceManager {


    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var ignite: Ignite

    @Autowired
    lateinit var cacheConcurency: IgniteCacheConcurency

    @Autowired
    lateinit var volumeMaService: VolumeMaIgniteService


//    public fun getCache(data: JsonObject): IgniteCache<String, Pair<Double, String>> {
//
//        var volumema = data.get("volumema").asInt
//
//        var cache = ignite.getOrCreateCache<String, Double>("MA$volumema:vol")
//        if (cache.size() == 0) {
//            process(data)
//        }
//        return cache
//    }


    public fun getCache(data: JsonObject): IgniteCache<String, Pair<Double, String>> {

        var key = getKey(data)

        var cache = ignite.getOrCreateCache<String, Pair<Double, String>>(key)
        if (cache.size() == 0) {
            loadall(data)
        }
        return cache
    }


    public fun runload(obj: JsonObject) {
        println("VolumeServiceManager --------runload--------$obj----------------")

        var getDataz = ::getData.curried()(obj.get("time").asInt)(obj.get("offset").asInt)
        var cache = ignite.getOrCreateCache<String, Double>(getKey(obj))

        stocklist.toObservable()
                //  .observeOn(Schedulers.computation())
                .map(getDataz)
                //.map(volumeMaService::movingavergae)
                .map {

                    Pair<String, Double>(it[0].code, volumeMaService.movingavergae(it))
                }

                .subscribe {
                    println("Received RESULT $it")

                    cache.put(it.first, it.second)
                }
    }

    /*
    if today then offset = 0
    if 2 days then offset = 1

     */
    private fun getData(time: Int, offset: Int, code: String): List<CoreData> {
        println("Received $code ----------------${Thread.currentThread().name}")
        println("VolumeServiceManager ----------------$time-------$offset---------")

        var z = ignitecache.values(" where code=?  order by date desc  LIMIT ?  OFFSET ? ", arrayOf(code, "$time", "$offset"))
        return z
    }

    private fun getKey(data: JsonObject) = "VOL${data.get("rsi")}-${data.get("offset")}"


    fun loadall(obj: JsonObject) {
        println("--------LOAD ALL  VOLUME----------")
        cacheConcurency.process(getKey(obj), obj, ::runload)
    }


}










