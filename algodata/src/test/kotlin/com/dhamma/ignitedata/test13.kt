package com.dhamma.ignitedata

import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.CoreStock
import com.dhamma.pesistence.entity.data.QCoreStock
import com.dhamma.pesistence.entity.repo.DataRepo
import com.dhamma.pesistence.entity.repo.StockRepo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate


@RunWith(SpringRunner::class)
@SpringBootTest
class test13 {


    @Autowired
    lateinit var dataRepo: DataRepo

    @Test
    fun getMetaData() {
        stock("ORG.AX").forEach(::println)
    }

    private fun stock(code: String): List<MyData> {
        var data = mutableListOf<MyData>();

        for (i in 0.rangeTo(14)) {

            // var no = 14 * i
            println("--------$i--------")
            var series = dataRepo.findbyCodeOffset(code, i)
            var num = Calc().calculateRsi(series.reversed())
            data.add(MyData(code, series[0].date, num))


        }
        return data
    }


    @Autowired
    lateinit var stockRepo: StockRepo

    private fun top300(): Iterable<CoreStock> {
        return stockRepo.findAll(QCoreStock.coreStock.top.eq("300"))
    }


}

data class MyData(var code: String, var date: LocalDate, var ris: Double)