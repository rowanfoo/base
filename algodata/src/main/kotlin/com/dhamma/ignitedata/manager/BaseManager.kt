package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.concurrency.IgniteCacheConcurency
import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.dhamma.pesistence.entity.repo.HistoryIndicatorsRepo
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate
import kotlin.streams.toList


open abstract class BaseManager {
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


    abstract fun runload(obj: JsonObject)


    fun loadall(obj: JsonObject) {
        println("--------LOAD ALL  RSI----------")
        println("------------RSIServiceManager---------------------LOAD ALL -------${obj}--")
//        cacheConcurency.process(getKey(obj), obj, ::runload)
        cacheConcurency.process("algoimport_run", obj, ::runload)
    }


}
