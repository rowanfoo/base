package com.dhamma.ignitedata.service

import com.dhamma.ignitedata.utility.TA4J
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TA4JService : BaseService() {
    @Autowired
    lateinit var tA4J: TA4J

    fun getCountAboveMA(code: String, ma: Int, counter: Double, sensitive: Double): String {
        return tA4J.sMAIndicatorAbovePercent(getData(ma * 2, code), ma, counter, sensitive)
    }

}
