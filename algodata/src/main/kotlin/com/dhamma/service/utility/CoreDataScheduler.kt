package com.dhamma.service.utility

import com.dhamma.service.algodata.CoreDataService
import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.base.ignite.util.IgniteUtility
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.repo.DataRepo
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
    lateinit var coreDataService: CoreDataService


    @Scheduled(cron = "0 19 15 ? * MON-FRI", zone = "GMT-8")
    fun ignitecache() {
        println("-----------------LOAD---SCHEUDULER-------------")
        println("-----------------PRE ---SIZE-----${ignitecache.size()}--------")
        igniteUtility.clearalldata()
        println("-----------------PRE ---SIZE-----${ignitecache.size()}--------")


        var data = coreDataService.get2yeardate()
        data.forEach { ignitecache.save("${it.code}:${it.date}", it) }

        println("-----------------LOAD---SIZE-----${ignitecache.size()}--------")


    }

}