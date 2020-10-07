package com.dhamma.ignitedata.service


import com.dhamma.pesistence.entity.data.*
import com.dhamma.pesistence.entity.repo.HistoryIndicatorsRepo
import com.dhamma.pesistence.entity.repo.JobRepo
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate
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


    fun todaytype(type: IndicatorType): List<HistoryIndicators> {
        return historyIndicatorsRepo.findAll(
                QHistoryIndicators.historyIndicators.date.eq(today()).and(
                        QHistoryIndicators.historyIndicators.type.eq(type)
                )
        ).toList()
    }

    fun todaytypeid(typeid: String): List<HistoryIndicators> {
        return historyIndicatorsRepo.findAll(
                QHistoryIndicators.historyIndicators.date.eq(today()).and(
                        QHistoryIndicators.historyIndicators.type_id.eq(typeid.toLong())
                )
        ).toList()
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
}
