package com.dhamma.service.algodata

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.News
import com.dhamma.pesistence.entity.data.QNews
import com.dhamma.pesistence.entity.repo.NewsRepo
import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class NewsService {

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var ignite: Ignite

    @Autowired
    lateinit var newsRepo: NewsRepo


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


    fun getCode(a: JsonObject): List<News> {
        var code: String = a.get("code").asString
        var date: String = a.get("date").asString


        var cache = ignite.getOrCreateCache<String, List<News>>("NEWS:$date")
        if (cache.size() == 0) {
            process(a)
            cache = ignite.getOrCreateCache<String, List<News>>("NEWS:$date")
            //  println("--------NEWS------------------$code----------${newcache.get(code)}")
            //return newcache.get(code)
        }
        println("--------NEWS CACHE------------------$code----------${cache.get(code)}")
//        return cache.get(code)
        // return cache.getAndPutIfAbsent(code, listOf())

        return cache.get(code) ?: listOf()
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
                println("-------------------$key----------------${data[0].title}")
                cache.put(key, data)
            }
        }
    }


}