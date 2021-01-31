package com.dhamma.ignitedata.manager

package com.dhamma.ignitedata.manager

import arrow.syntax.function.curried
import com.dhamma.ignitedata.service.PriceService
import com.dhamma.pesistence.entity.data.HistoryIndicators
import com.dhamma.pesistence.entity.data.IndicatorType
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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
        var operator: String = useralgo.get("days").asString
        var typeid = useralgo.get("id").asLong
        var userid = useralgo.get("userid").asString

        var getResultLesser = coreDataIgniteService::lesserPercentlt.curried()(today().toString())
        var getResultGreater = coreDataIgniteService::lesserPercentlt.curried()(today().toString())

        var list = when {
            (operator == ">") -> getResultGreater(percent)
            (operator == "<") -> getResultLesser(percent)
            else -> listOf()
        }

        var msg = when {
            (operator == ">") -> "UP"
            (operator == "<") -> "FALL"
            else -> ""
        }

        return list
                .map {
                    var x = HistoryIndicators
                            .builder().code(it.code)
                            .date(it.date)
                            .type(if (operator == ">") IndicatorType.PRICE_UP else IndicatorType.PRICE_FALL)
                            .value(it.changepercent)
                            .type_id(typeid)
                            .userid(userid)
                            .message("$msg  4%   ${"%.3f".format((it.changepercent * 100))} % ").build()
                    println("----------runload---------${x}----")
                    x
                }.toList()


    }


}





//
//stocklist.forEach { s: String ->
//
//    var today = coreDataIgniteService.today(s)
//    var zz = coreDataIgniteService.offsetrecords(s, 80, 1)
//    var high = zz.maxBy { coreData -> coreData.close }
//
//    if (today.close > high!!.close) {
//        println("-------HIGH------------$s")
//    }
//
//}








