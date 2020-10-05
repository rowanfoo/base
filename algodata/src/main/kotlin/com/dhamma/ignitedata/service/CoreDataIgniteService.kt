package com.dhamma.ignitedata.service

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.QCoreData
import com.dhamma.pesistence.entity.repo.DataRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class CoreDataIgniteService {

    @Autowired
    lateinit var dataRepo: DataRepo

    @Autowired
    lateinit var ignitecache: IgniteRepo<CoreData>

    fun get2yeardate(): Iterable<CoreData> = dataRepo.findAll(QCoreData.coreData.date.gt(LocalDate.now().minusYears(2)))

    fun dategt(code: String, date: String): List<CoreData> = ignitecache.values(" where code=?  and  date > ?  ", arrayOf(code, date))

    fun today(code: String): CoreData = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(code, "1")).first()
    fun today(): CoreData = ignitecache.values(" where code='BHP.AX'  order by date desc  LIMIT ? ", arrayOf("1")).first()
    fun changePercentlt(date: String, fallpercent: String): List<CoreData> = ignitecache.values(" where  date=? and changepercent < ?", arrayOf(date, fallpercent))
    fun lesserPercentlt(date: String, fallpercent: String): List<CoreData> = ignitecache.values(" where  date=? and changepercent < ?", arrayOf(date, fallpercent))


    fun sixMonthdata(code: String): Iterable<CoreData> = ignitecache.values("where code=?  order by date desc  LIMIT ? ", arrayOf(code, "120"))
    fun threehundred(code: String): Iterable<CoreData> = ignitecache.values("where code=?  order by date desc  LIMIT ? ", arrayOf(code, "300"))


    fun records(code: String, no: Int) = ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(code, "$no"))


    fun cleasdata() {
        ignitecache.removeall()
    }


    fun getDatabyLimit(time: Int, code: String): List<CoreData> = ignitecache.values(" where code=?  order by date desc  LIMIT ?  ", arrayOf(code, "$time"))


    fun reload() {
        ignitecache.removeall()
        var mydata = dataRepo.findAll(QCoreData.coreData.date.gt(LocalDate.now().minusYears(2)))
        mydata.forEach { ignitecache.save("${it.code}:${it.date}", it) }


    }

}
