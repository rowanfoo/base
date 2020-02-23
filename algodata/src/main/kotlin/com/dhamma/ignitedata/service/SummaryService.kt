package com.dhamma.ignitedata.service


import com.dhamma.ignitedata.utility.Calc
import com.dhamma.pesistence.entity.data.*
import com.dhamma.pesistence.entity.repo.DataRepo
import com.dhamma.pesistence.entity.repo.StockRepo
import com.dhamma.pesistence.entity.repo.SummaryRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger

@Component
public class SummaryService {

    @Autowired
    lateinit var dataRepo: DataRepo

    @Autowired
    lateinit var summaryRepo: SummaryRepo

    @Autowired
    lateinit var stockRepo: StockRepo

    var rsicount = AtomicInteger()

    fun updateToday() {
        var date: LocalDate = LocalDate.now()
        top300().forEach {
            var series = dataRepo.findbyCodeOffset(it.code, 0)
            if (!series.isEmpty()) {
                date = series[0].date
                var rsino = rsi(series)
                addrsi(rsino)
            }
        }
        addup(date)
        adddown(date)
        addfourcount(date)
        var sum = Summary.builder()
                .rsi(rsicount.toInt())
                .date(date)
                .down(adddown(date))
                .up(addup(date))
                .fourdown(addfourcount(date)).build()

        summaryRepo.saveAndFlush(sum)
        reset()
    }


    private fun rsi(series: List<CoreData>): Double = Calc().calculateRsi(series.reversed())


    private fun reset() = rsicount.set(0)

    private fun addup(date: LocalDate): Int = dataRepo.findAll((QCoreData.coreData.date.eq(date)).and(QCoreData.coreData.changepercent.gt(0.0))).count()


    private fun adddown(date: LocalDate): Int = dataRepo.findAll((QCoreData.coreData.date.eq(date)).and(QCoreData.coreData.changepercent.lt(0.0))).count()


    private fun addfourcount(date: LocalDate): Int = dataRepo.findAll((QCoreData.coreData.date.eq(date)).and(QCoreData.coreData.changepercent.lt(-0.04))).count()


    private fun addrsi(rsi: Double) {
        if (rsi < 30) {
            var no = rsicount.incrementAndGet()
        }

    }


    private fun top300(): Iterable<CoreStock> {
        return stockRepo.findAll(QCoreStock.coreStock.top.eq("300"))
    }


}

