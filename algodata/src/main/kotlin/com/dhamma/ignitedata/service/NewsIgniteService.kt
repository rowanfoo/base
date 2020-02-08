package com.dhamma.ignitedata.service

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.News
import com.dhamma.pesistence.entity.data.QNews
import com.dhamma.pesistence.entity.repo.NewsRepo
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class NewsIgniteService {

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var ignite: Ignite

    @Autowired
    lateinit var newsRepo: NewsRepo
    @Autowired
    lateinit var coreDataIgniteService: CoreDataIgniteService
    object Lock

    fun getCache(a: JsonObject): IgniteCache<String, List<News>> {
        var code: String = a.get("code").asString
        var date: String = a.get("date").asString

        var cache = ignite.getOrCreateCache<String, List<News>>("NEWS:$date")
        if (cache.size() == 0) {
            process(a)
            cache = ignite.getOrCreateCache<String, List<News>>("NEWS:$date")
        }
        return cache
    }
//    fun newsToday(code: String): JsonNode {
//        var mapper = ObjectMapper()
//        var data = coreDataIgniteService.today(code)
//
//        var a = JsonObject()
//        a.addProperty("code", code)
//        a.addProperty("date", data.date.toString())
//        return mapper.convertValue(getCode(a), JsonNode::class.java)
//    }

    fun newsToday(code: String):  List<News> {
        var mapper = ObjectMapper()
        var data = coreDataIgniteService.today(code)

        var a = JsonObject()
        a.addProperty("code", code)
        a.addProperty("date", data.date.toString())
        return getCode(a)
    }

    fun getCode(a: JsonObject): List<News> {
        var code: String = a.get("code").asString
        var date: String = a.get("date").asString


        var cache = ignite.getOrCreateCache<String, List<News>>("NEWS:$date")
        if (cache.size() > 0) {
            return cache.get(code) ?: listOf()
        }

        synchronized(Lock) {/// will later refactor , this will prevent multiple thread excute process.
            if (cache.size() == 0) {
                process(a)
                cache = ignite.getOrCreateCache<String, List<News>>("NEWS:$date")
                return cache.get(code) ?: listOf()
            }
         }
        return listOf()
      }

    fun process(a: JsonObject) {
        var code: String = a.get("code").asString
        var date: String = a.get("date").asString


//         var codes="";
//        stocklist.forEach {
//            codes = codes + "$it,"
//        }
//
//        var codes = stocklist.joinToString { it }
//        println("--------load--------$codes--")
        var cache = ignite.getOrCreateCache<String, List<News>>("NEWS:$date")

        if (cache.size() == 0) {
//            stocklist.parallelStream().forEach {
//                // var series = ignitecache.get("$it")
//                println("--------load----CODE-------------$it")
//                var series = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(it, "$no"))
//
//                println("--------load----SIZE-------------${series.size}")
//
//                var num = Calc().mocingaverage(no, series, mode)
//
//                println("--------AVG--------$it------------$num")
//                cache.put("$it", num)
//
//            }
            //  = newsRepo.findAll((QNews.news.code.in(stocklist) )   )

            var tables = newsRepo.findAll(QNews.news.code.`in`(stocklist).and(QNews.news.date.eq(LocalDate.parse(date)))).groupBy { it.code }
            //            var tables = newsRepo.findAll(QNews.news.date.eq(LocalDate.parse(date)).and(QNews.news.code.`in`() )   ).groupBy { it.code }

//            println("--------load--------${tables.size}--")

            tables.forEach { (key, data) ->
                //  println("-------------------$key----------------${data[0].title}")
                cache.put(key, data)
            }
        }
    }


}