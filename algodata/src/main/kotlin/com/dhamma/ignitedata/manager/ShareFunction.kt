package com.dhamma.ignitedata.manager

import com.dhamma.pesistence.entity.data.CoreData

 fun getData(time: Int, offset: Int, code: String): List<CoreData> {
    //  println("Received $code ----------------${Thread.currentThread().name}")
    println("------getData-----${code}-----------")
    var z = ignitecache.values(" where code=?  order by date desc  LIMIT ?  OFFSET ? ", arrayOf(code, "$time", "$offset"))
    println("------getData-----${z.size}-----------")
    return z
}

