package com.dhamma.ignitedata.manager


import com.dhamma.ignitedata.service.PriceService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.streams.toList

@Component
class PriceHighManager : BaseManager() {

    @Autowired
    lateinit var stocklist: List<String>

    @Autowired
    lateinit var priceService: PriceService


    public fun add(data: JsonObject) {
        loadall(data)
    }

    override fun runload(useralgo: JsonObject): List<HistoryIndicators> {
        var days: String = useralgo.get("days").asString
        var typeid = useralgo.get("id").asLong
        var userid = useralgo.get("userid").asString

        return stocklist.parallelStream()
                .map {
                    coreDataIgniteService.today(it)
                }
                .filter {
                    var zz = coreDataIgniteService.offsetrecords(it.code, days.toInt(), 1)
                    var high = zz.maxBy { coreData -> coreData.close }
                    it.close > high!!.close
                }
                .map {
                    var x = HistoryIndicators
                            .builder().code(it.code)
                            .date(it.date)
                            .type(IndicatorType.PRICE_HIGH)
                            .value(it.changepercent)
                            .type_id(typeid)
                            .userid(userid)
                            .message("Today up first time up above days $days").build()
                    println("----------runload---------${x}----")
                    x
                }.toList()
    }
}









