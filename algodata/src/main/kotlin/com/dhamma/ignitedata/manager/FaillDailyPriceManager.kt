package com.dhamma.ignitedata.manager

/// find <7 days> fallen  <  day > for < %>
//fall 7 days
//

import com.dhamma.ignitedata.service.MAService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FaillDailyPriceManager : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var maService: MAService


    override fun runload(obj: JsonObject): List<HistoryIndicators> {
        println("-----********START****FaillDailyPriceManager**********------$obj--------")

        //var time = obj.get("time").asString
        var typeid = obj.get("id").asLong
        var userid = obj.get("userid").asString
        var days = obj.get("days").asInt
        var sensitive = obj.get("sensitive").asInt
        var percent = obj.get("percent").asDouble

        var list = coreDataIgniteService.lesserPercentlt(today().toString(), "0") // find all down today

        var result = list.map {
            it.code
        }
                .map {
                    var data = coreDataIgniteService.getDatabyLimit(days, it)
                    Pair(it, fnnodaydown(data))
                }
                .filter {
                    var (count, total) = it.second
                    (count >= 5) && (total < percent)
                }
                .map {
                    var x = HistoryIndicators
                            .builder().code(it.first)
                            .date(today())
                            .type(IndicatorType.PRICE_CONSEQ)
                            .value(it.second.second)
                            .type_id(typeid)
                            .userid(userid)
                            .message(" fallen for ${it.second.first} days for ${it.second.second} % fall  ").build()
                    x
                }.toList()
        return result
    }
}
