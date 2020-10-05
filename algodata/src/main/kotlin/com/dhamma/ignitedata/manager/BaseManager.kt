package com.dhamma.ignitedata.manager

import com.dhamma.base.ignite.concurrency.IgniteCacheConcurency
import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.repo.HistoryIndicatorsRepo
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import javax.transaction.Transactional

@Transactional
open abstract class BaseManager : IManager {
    @Autowired
    lateinit var coreDataIgniteService: CoreDataIgniteService

    @Autowired
    lateinit var historyIndicatorsRepo: HistoryIndicatorsRepo

    @Autowired
    lateinit var cacheConcurency: IgniteCacheConcurency


    fun getData(time: Int, code: String): List<CoreData> {
        //  println("Received $code ----------------${Thread.currentThread().name}")
//        println("------getData-----${code}-----------")
//        var z = ignitecache.values(" where code=?  order by date desc  LIMIT ?  ", arrayOf(code, "$time"))
//        println("------getData-----${z.size}-----------")
//        return z

        return coreDataIgniteService.getDatabyLimit(time, code)
    }


    fun today() = coreDataIgniteService.today().date


    fun addData(data: List<HistoryIndicators>) {
        historyIndicatorsRepo.saveAll(data)
    }


    fun today(code: String) = coreDataIgniteService.today(code)


    abstract fun runload(obj: JsonObject): List<HistoryIndicators>

    override fun loadall(obj: JsonObject) {
//        println("--------LOAD ALL  RSI----------")
        //      println("------------RSIServiceManager---------------------LOAD ALL -------${obj}--")
//        cacheConcurency.process(getKey(obj), obj, ::runload)

//        var value = if (obj.get("time") != null) obj.get("time").asString else obj.get("price").asString
//        value = value + "-" + obj.get("type").asString
//        cacheConcurency.process("algoimport_run $value", obj, ::runload)
// for now DONT do any concurrency later can also do using DB , with job tabls

        var list = runload(obj)
        addData(list)


    }


}
