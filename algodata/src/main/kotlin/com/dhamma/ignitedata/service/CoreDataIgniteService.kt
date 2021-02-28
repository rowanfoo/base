package com.dhamma.ignitedata.service

import com.dhamma.base.ignite.IgniteRepo
import com.dhamma.pesistence.entity.data.CoreData
import com.dhamma.pesistence.entity.data.QCoreData
import com.dhamma.pesistence.entity.data.QFundamental
import com.dhamma.pesistence.entity.repo.DataRepo
import com.dhamma.pesistence.entity.repo.FundamentalRepo
import org.apache.ignite.cache.query.FieldsQueryCursor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class CoreDataIgniteService {

    @Autowired
    lateinit var dataRepo: DataRepo

    @Autowired
    lateinit var fundamentalRepo: FundamentalRepo

    @Autowired
    public lateinit var ignitecache: IgniteRepo<CoreData>

    fun get2yeardate(): Iterable<CoreData> = dataRepo.findAll(QCoreData.coreData.date.gt(LocalDate.now().minusYears(2)))

    fun dateeq(code: String, date: String): CoreData =
        ignitecache.values(" where code=?  and  date=?  ", arrayOf(code, date)).first()

    fun dategt(code: String, date: String): List<CoreData> =
        ignitecache.values(" where code=?  and  date > ?  ", arrayOf(code, date))

    fun datelseq(code: String, date: String): List<CoreData> =
        ignitecache.values(" where code=?  and  date <= ?  ", arrayOf(code, date))

    fun datelseqlimit(code: String, date: String, limit: String): List<CoreData> =
        ignitecache.values(" where code=?  and  date <= ? order by date desc  LIMIT ? ", arrayOf(code, date, limit))


    fun today(code: String): CoreData =
        ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(code, "1")).first()

    fun today(): CoreData =
        ignitecache.values(" where code='BHP.AX'  order by date desc  LIMIT ? ", arrayOf("1")).first()

    fun changePercentlt(date: String, fallpercent: String): List<CoreData> =
        ignitecache.values(" where  date=? and changepercent < ?", arrayOf(date, fallpercent))

    fun lesserPercentlt(date: String, fallpercent: String): List<CoreData> =
        ignitecache.values(" where  date=? and changepercent < ?", arrayOf(date, fallpercent))


    fun sixMonthdata(code: String): Iterable<CoreData> =
        ignitecache.values("where code=?  order by date desc  LIMIT ? ", arrayOf(code, "120"))

    fun threehundred(code: String): Iterable<CoreData> =
        ignitecache.values("where code=?  order by date desc  LIMIT ? ", arrayOf(code, "300"))


    fun records(code: String, no: Int) =
        ignitecache.values(" where code=?  order by date desc  LIMIT ? ", arrayOf(code, "$no"))

    fun offsetrecords(code: String, no: Int, offset: Int) =
        ignitecache.values(" where code=?  order by date desc  LIMIT ? offset ? ", arrayOf(code, "$no", "$offset"))

    fun cleasdata() {
        ignitecache.removeall()
    }

    // from today to last period HIGH
    fun priceperiodprecent(code: String, mode: String): Double {

        var date: String = if (mode == "week") LocalDate.now().minusWeeks(1).toString()
        else if (mode == "month") LocalDate.now().minusMonths(1).toString()
        else LocalDate.now().minusMonths(3).toString()

        var z = ignitecache.fields(
            "select max(CoreData.close) from CoreData where CoreData.code=? and date >= ?",
            arrayOf<String>(code, date)
        )
        //       println("----------priceformonthtodayprecent-----------${z}----")
        //     println("----------today-----------${today(code)}----")

        var today = today(code).close
        var high = getsinglefield(z).toDouble()

        return (today - high) / high
    }

    fun pricethisweek(code: String): Map<String, Double> {
        var data = getDatabyLimit(5, code)
        var high = data.maxBy { it.close }
        var min = data.minBy { it.close }
        var today = data[0].close
        var start = data.last().close
        return mapOf<String, Double>(
            "highthisweek" to high!!.close,
            "minthisweek" to min!!.close,
            "endthisweek" to today,
            "startthisweek" to start
        )
    }

    fun getDatabyLimit(time: Int, code: String): List<CoreData> =
        ignitecache.values(" where code=?  order by date desc  LIMIT ?  ", arrayOf(code, "$time"))

    fun reload() {
        ignitecache.removeall()
        var mydata = dataRepo.findAll(QCoreData.coreData.date.gt(LocalDate.now().minusYears(2)))
        mydata.forEach { ignitecache.save("${it.code}:${it.date}", it) }
    }

    private fun getsinglefield(data: FieldsQueryCursor<List<*>>): String {
        var d = ""
        data.forEach {
            it.forEach {
                d = it.toString()
            }
        }
        return d
    }

    fun correctionfromHigh(code: String): Map<String, Double> {
        var today = today(code).close
        var yearhigh = fundamentalRepo.findOne(QFundamental.fundamental.code.eq(code)).get().yearHighPrice
        return mapOf<String, Double>("today" to today, "yearhigh" to yearhigh)
    }
}
