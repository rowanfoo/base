package com.dhamma.ignitedata.manager

import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.UserConfig
import com.google.gson.JsonObject
import java.util.regex.Pattern


fun rsi(series: List<CoreData>): Double {
    var num = Calc().calculateRsi(series.reversed())
    return String.format("%.1f", num).toDouble()
}

fun getRange(series: List<CoreData>): String {
    var max = series.maxBy { it.close }!!.close
    var min = series.minBy { it.close }!!.close
    var percent = String.format("%.1f", (((max - min) / max) * 100))
    return "$max - $min   $percent"
}

fun threeElems(zz: String): Triple<String, String, String> {
    val r = Pattern.compile("<|>|=")
    val m = r.matcher(zz)
    m.find()
    println("Start index: " + m.start())
    println("----------------" + zz.substring(0, m.start()))
    println("----------------" + zz.substring(m.start(), m.end()))
    println("----------------" + zz.substring(m.end(), zz.length))
    println(" End index: " + m.end())
    println(" Found: " + m.group())
    return Triple(zz.substring(0, m.start()), zz.substring(m.start(), m.end()), zz.substring(m.end(), zz.length))
}


fun fndelimeterPair(zz: String): Pair<String, String> {
    var a = zz.split(";")
    return Pair(a[0], a[1])
}


public fun twoElems(zz: String): Pair<String, String> {
    val r = Pattern.compile("<|>|=")
    val m = r.matcher(zz)
    m.find()
    println("Start index: " + m.start())
    println("----------------" + zz.substring(0, m.start()))
    println("----------------" + zz.substring(m.start(), m.end()))
    println(" End index: " + m.end())
    println(" Found: " + m.group())
    return Pair(zz.substring(0, m.start()), zz.substring(m.start(), m.end()))
}

fun fnmapricefromconfig(id: Long, operator: String, time: String, percent: String): JsonObject {

    var content = JsonObject()
    content.addProperty("id", id)
    content.addProperty("userid", "rowan")
    content.addProperty("type", "ma")
    content.addProperty("mode", "price")
    content.addProperty("operator", operator)
    content.addProperty("time", time)
    content.addProperty("percent", percent)
    return content
}


fun fnnodaydown(data: List<CoreData>): Pair<Int, Double> {
    var count = 0
    var totaldown = 0.0
    data.forEach {
        if (it.changepercent < 0) {
            count++
            totaldown += it.changepercent
        }
    }
    return Pair(count, totaldown)
}

fun fnbasicUserConfigContent(config: UserConfig): JsonObject {
    var content = JsonObject()
    content.addProperty("id", config.id)
    content.addProperty("userid", config.userid)
    content.addProperty("type", config.getType().name)
    return content
}
