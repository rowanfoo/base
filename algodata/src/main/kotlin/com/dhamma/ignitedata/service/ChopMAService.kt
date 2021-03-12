package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.TA4J
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

/*
Feature this is to find where price have not gone anyway , consolidation phase flat MA , traders are all bored out and suddenly it break out . like OMH.AX
 */

@Component
class ChopMAService : BaseService() {
    //  When the CHOP is above 61.8 == RANGE boung
    //  When the CHOP is below 38.2 === TREND Up or Down

    @Autowired
    lateinit var ta4j: TA4J

    fun getResult(useralgo: JsonObject, data: List<CoreData>): Optional<JsonObject> {

        var content = JsonObject()
        content.addProperty("code", data[0].code)

        var time = useralgo.get("time").asInt
        var sensitive = useralgo.get("sensitive").asDouble
        var algopercent = useralgo.get("percent").asDouble
        var ma = useralgo.get("ma").asInt

        var d = ta4j.closePrice(data)
        var z = ta4j.sMAIndicator(data, ma)
        var t = ta4j.chopIndicator(data)

        var x = t.timeSeries.endIndex
        var y = t.timeSeries.endIndex - time


        var count = 0
        var list = mutableListOf<String>()
        for (i in x downTo y) {
            if (t.getValue(i).doubleValue() > sensitive) count++
            if (t.getValue(i).doubleValue() > sensitive) list.add(percentformat(t.getValue(i).doubleValue()))

        }
        // maybe today it has rise and chopma would have gone down.
        if (count == (time - 1)) {
            var today = d.getValue(x).doubleValue()
            var matoday = z.getValue(x).doubleValue()

            var percent = (today - matoday) / matoday

            if (percent > algopercent) {
                content.addProperty("chop", list.joinToString())
                content.addProperty("price", today)
                content.addProperty("ma", matoday)
                content.addProperty("percent", percentformat(percent * 100))
                return Optional.of(content)
            }
        }
        return Optional.empty()
    }
}
