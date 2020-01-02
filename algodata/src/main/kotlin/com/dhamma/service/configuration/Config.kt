package com.dhamma.service.configuration

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.QCoreData
import com.dhamma.pesistence.entity.repo.DataRepo
import com.dhamma.pesistence.entity.repo.StockRepo
import org.apache.ignite.Ignite
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
class Config {
    //    @Autowired
//    lateinit var coreDataService: CoreDataService
    @Autowired
    lateinit var dataRepo: DataRepo
    @Autowired
    lateinit var stockrepo: StockRepo

    @Bean
    fun ignitecache(ignite: Ignite): IgniteRepo<CoreData> {
        println("-----------------LOAD---ignitecache-------------")
//        var igniterepo = IgniteRepo<CoreDataService>(ignite, Class.forName("com.dhamma.pesistence.data.data.CoreDataService").kotlin)
        var igniterepo = IgniteRepo<CoreData>(ignite, CoreData())

        var mydata = dataRepo.findAll(QCoreData.coreData.date.gt(LocalDate.now().minusYears(2)))
        mydata.forEach { igniterepo.save("${it.code}:${it.date}", it) }

        println("-----------------LOAD---SIZE-----${igniterepo.size()}--------")


        var querydata = igniterepo.values(" where code=? ", arrayOf("BHP.AX"))
        println("----------------------data---------------------${querydata.size}-")

        return return igniterepo


    }

    @Bean
    fun stocklist(): List<String> {
        println("------------------------------STOCKLIST--------------")
//        var d = dataRepo.findOne(QCoreData.coreData.id.eq(1L))
//        println("------------------------------COREDATA----------$d----")
        var list = stockrepo.findAll()

        return list.map { it.code }.toList()

//        return listOf("BHP.AX", "RIO.AX", "NAB.AX", "WBC.AX", "CBA.AX")
//        return listOf("ABC.AX", "BHP.AX", "WBC.AX")
        //   return listOf("BHP.AX")

    }


}