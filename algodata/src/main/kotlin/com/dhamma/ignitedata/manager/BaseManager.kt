package com.dhamma.ignitedata.manager

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import org.springframework.beans.factory.annotation.Autowired


class BaseManager {
    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>


    private fun getData(time: Int, code: String): List<CoreData> {
        //  println("Received $code ----------------${Thread.currentThread().name}")
        println("------getData-----${code}-----------")
        var z = ignitecache.values(" where code=?  order by date desc  LIMIT ?  ", arrayOf(code, "$time"))
        println("------getData-----${z.size}-----------")
        return z
    }

//    private fun addData(){
//
//    }


}
