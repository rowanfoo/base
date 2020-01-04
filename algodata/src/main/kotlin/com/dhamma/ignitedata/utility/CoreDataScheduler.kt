package com.dhamma.ignitedata.utility

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.util.IgniteUtility
import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.CoreStock
import com.dhamma.pesistence.entity.repo.DataRepo
import com.dhamma.pesistence.entity.repo.StockRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class CoreDataScheduler {

    @Autowired
    lateinit var dataRepo: DataRepo

    @Autowired
    lateinit var igniteUtility: IgniteUtility

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    @Autowired
    lateinit var coreDataService: CoreDataIgniteService
    @Autowired
    lateinit var stockrepo: StockRepo

    @Autowired
    lateinit var ignitecachestock: IgniteRepo<CoreStock>

    @Scheduled(cron = "0 19 15 ? * MON-FRI", zone = "GMT-8")
    fun ignitecache() {
        println("-----------------LOAD---SCHEUDULER-------------")
        println("-----------------PRE ---SIZE-----${ignitecache.size()}--------")
        igniteUtility.clearalldata()
        println("-----------------PRE ---SIZE-----${ignitecache.size()}--------")


        var data = coreDataService.get2yeardate()
        data.forEach { ignitecache.save("${it.code}:${it.date}", it) }


        var list = stockrepo.findAll().forEach {
            ignitecachestock.save("${it.code}", it)
        }


    }

}