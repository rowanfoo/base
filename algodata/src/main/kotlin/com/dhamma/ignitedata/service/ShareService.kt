package com.dhamma.ignitedata.service

import com.dhamma.pesistence.entity.data.CoreData
import org.springframework.stereotype.Component

@Component
class ShareService {


    fun getRange(series: List<CoreData>): String {
        var max = series.maxBy { it.close }!!.close
        var min = series.minBy { it.close }!!.close
        var percent = String.format("%.1f", (((max - min) / max) * 100))
        return "$max - $min   $percent"
    }


}
