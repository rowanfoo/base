package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.MFIIndicator
import com.dhamma.ignitedata.utility.TA4J
import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*


@Component
class MFIIndicatorService : BaseService() {
    @Autowired
    lateinit var ta4j: TA4J

    fun getResult(useralgo: JsonObject, data: List<CoreData>): Optional<JsonObject> {

        var content = JsonObject()
        content.addProperty("code", data[0].code)

        var mfi = useralgo.get("mfi").asDouble

        var t = ta4j.typicalPrice(data)
        var rs = ta4j.rSIIndicator(data)
        var z = MFIIndicator(t.timeSeries, t, 14)
        var x = z.timeSeries.endIndex

        if (z.getValue(x).doubleValue() < mfi) {
            content.addProperty("MFI", percentformat(z.getValue(x).doubleValue()))
            content.addProperty("RSI", percentformat(z.getValue(x).doubleValue()))
            return Optional.of(content)
        }
        return Optional.empty()
    }
}
