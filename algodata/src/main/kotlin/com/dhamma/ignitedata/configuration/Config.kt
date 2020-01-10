package com.dhamma.ignitedata.configuration

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.CoreStock
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
        var igniterepo = IgniteRepo<CoreData>(ignite, CoreData())

        var mydata = dataRepo.findAll(QCoreData.coreData.date.gt(LocalDate.now().minusYears(2)))
        mydata.forEach { igniterepo.save("${it.code}:${it.date}", it) }
        var querydata = igniterepo.values(" where code=? ", arrayOf("BHP.AX"))
        return return igniterepo
    }

    @Bean
    fun stocklist(): List<String> {
        var list = stockrepo.findAll()
        return list.map { it.code }.toList()
    }

    @Bean
    fun ignitecachestock(ignite: Ignite): IgniteRepo<CoreStock> {
        var igniterepo = IgniteRepo<CoreStock>(ignite, CoreStock())
        var list = stockrepo.findAll().forEach {
            igniterepo.save("${it.code}", it)
        }
        return return igniterepo
    }

}