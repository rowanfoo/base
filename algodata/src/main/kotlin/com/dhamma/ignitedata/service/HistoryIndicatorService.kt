package com.dhamma.ignitedata.service


import com.dhamma.pesistence.entity.data.*
import com.dhamma.pesistence.entity.repo.HistoryIndicatorsRepo
import com.dhamma.pesistence.entity.repo.JobRepo
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext


@Component
class HistoryIndicatorService {

    @Autowired
    lateinit var jobrepo: JobRepo

    @Autowired
    lateinit var historyIndicatorsRepo: HistoryIndicatorsRepo

    @PersistenceContext
    lateinit var em: EntityManager

    fun today(): LocalDate {
        val query = JPAQueryFactory(em)
        return (query.from(QJob.job)
            .orderBy(QJob.job.date.desc()).limit(1).fetch() as List<Job>)[0].date
    }

    fun dateExsits(date: String): LocalDate {
        var date = jobrepo.findOne(QJob.job.date.eq(LocalDate.parse(date)))
        return if (date.isPresent) date.get().date
        else {
            return today()
        }
    }


    fun todaytype(type: IndicatorType): List<HistoryIndicators> {
        return historyIndicatorsRepo.findAll(
            QHistoryIndicators.historyIndicators.date.eq(today()).and(
                QHistoryIndicators.historyIndicators.type.eq(type)
            )
        ).toList()
    }

    fun todaytypeid(typeid: String, date: Optional<String>): List<HistoryIndicators> {
        var mydate = if (date.isPresent()) LocalDate.parse(date.get()) else today()

        return historyIndicatorsRepo.findAll(
            QHistoryIndicators.historyIndicators.date.eq(mydate).and(
                QHistoryIndicators.historyIndicators.type_id.eq(typeid.toLong())
            )
        ).toList()
    }

    fun datebetweentypeid(
        typeid: String,
        date1: String,
        date2: String,
        page: Pair<Int, Int>
    ): Page<HistoryIndicators> {
        return historyIndicatorsRepo.findAll(
            QHistoryIndicators.historyIndicators.date.between(LocalDate.parse(date1), LocalDate.parse(date2)).and(
                QHistoryIndicators.historyIndicators.type_id.eq(typeid.toLong())
            ), PageRequest.of(page.first, page.second)
        )
    }

    fun datetype(type: IndicatorType, date: LocalDate): List<HistoryIndicators> {
        return historyIndicatorsRepo.findAll(
            QHistoryIndicators.historyIndicators.date.eq(date).and(
                QHistoryIndicators.historyIndicators.type.eq(type)
            )
        ).toList()
    }

    fun datetypeid(typeid: String, date: LocalDate): List<HistoryIndicators> {
        return historyIndicatorsRepo.findAll(
            QHistoryIndicators.historyIndicators.date.eq(date).and(
                QHistoryIndicators.historyIndicators.type_id.eq(typeid.toLong())
            )
        ).toList()
    }

    fun todaycode(code: String): List<HistoryIndicators> {
        return historyIndicatorsRepo.findAll(
            QHistoryIndicators.historyIndicators.date.eq(today()).and(
                QHistoryIndicators.historyIndicators.code.eq(code)
            )
        ).toList()
    }

    //    fun datecodes(codes: List<String>, date: LocalDate): List<HistoryIndicators> {
    fun datecodes(codes: List<String>, date: LocalDate): Map<String, List<HistoryIndicators>> {

        return historyIndicatorsRepo.findAll(
            QHistoryIndicators.historyIndicators.date.eq(date).and(
                QHistoryIndicators.historyIndicators.code.`in`(codes)
            )
        ).groupBy { historyIndicators: HistoryIndicators? -> historyIndicators!!.code }


    }


}
