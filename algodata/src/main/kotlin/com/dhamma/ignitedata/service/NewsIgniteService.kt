package com.dhamma.ignitedata.service

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.News
import com.dhamma.pesistence.entity.data.QNews
import com.dhamma.pesistence.entity.repo.NewsRepo
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

    fun newsToday(code: String): List<News> {
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
        var cache = ignite.getOrCreateCache<String, List<News>>("NEWS:$date")

        if (cache.size() == 0) {

            var tables = newsRepo.findAll(QNews.news.code.`in`(stocklist).and(QNews.news.date.eq(LocalDate.parse(date)))).groupBy { it.code }

            tables.forEach { (key, data) ->
                cache.put(key, data)
            }
        }
    }


}
