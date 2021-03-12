package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.TA4J
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

//Put a 14-period RSI on the same charts with the CHOP indicator on it.
// Draw a horizontal line at the 50 level of the RSI.
// When the CHOP is above 61.8 or in between the upper and lower limits, do nothing. When the CHOP is below 38.2, the lower threshold, take a trade according to the RSI.
// If the RSI is above 50 take a buy trade and in case the RSI is below 50 take a short sell trade. Keep a suitable stop loss or use some trailing stop method.

@Component
class ChopRSIService : BaseService() {
    @Autowired
    lateinit var ta4j: TA4J

    fun getResult(useralgo: JsonObject, data: List<CoreData>): Optional<JsonObject> {

        var content = JsonObject()
        content.addProperty("code", data[0].code)

        var sensitive = useralgo.get("sensitive").asDouble
        var rsi = useralgo.get("rsi").asDouble

        var d = ta4j.closePrice(data)
        var z = ta4j.rSIIndicator(data)
        var t = ta4j.chopIndicator(data)

        var x = t.timeSeries.endIndex
        if (t.getValue(x).doubleValue() < sensitive) {
            if (z.getValue(x).doubleValue() > rsi) {
                content.addProperty("chop", percentformat(t.getValue(x).doubleValue()))
                content.addProperty("rsi", percentformat(z.getValue(x).doubleValue()))
                return Optional.of(content)
            }
        }
        return Optional.empty()
    }
}
