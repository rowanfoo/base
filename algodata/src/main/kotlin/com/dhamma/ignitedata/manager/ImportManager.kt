package com.dhamma.ignitedata.manager

import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.ignitedata.service.NewsIgniteService
import com.dhamma.pesistence.entity.data.*
import com.dhamma.pesistence.entity.repo.JobRepo
import com.dhamma.pesistence.entity.repo.UserRepo
import com.google.gson.JsonObject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ImportManager {

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var maManager: MAManager

    @Autowired
    lateinit var rSIManager: RSIManager

    @Autowired
    lateinit var volumeManager: VolumeManager

    @Autowired
    lateinit var priceManager: PriceManager

    @Autowired
    lateinit var runningMAManger: RunningMAManger

    @Autowired
    lateinit var priceHighManager: PriceHighManager


    @Autowired
    lateinit var coreDataIgniteService: CoreDataIgniteService

    @Autowired
    lateinit var faillDailyPriceManager: FaillDailyPriceManager

    @Autowired
    lateinit var jobRepo: JobRepo


    @Autowired
    lateinit var newsIgniteService: NewsIgniteService

    @Autowired
    lateinit var chopMAManger: ChopMAManger


    private fun check(): Boolean {
        return jobRepo.findOne(QJob.job.date.eq(coreDataIgniteService.today().date)).isPresent()
    }


    public @Synchronized
    fun startimport() {

        var userconfig = user("rowan").userConfig
        var maconfig = user("rowan").getUserConfigType(IndicatorType.MA)
        var fmaconfig = user("rowan").getUserConfigType(IndicatorType.MAF)
        var price_conseqconfig = user("rowan").getUserConfigType(IndicatorType.PRICE_CONSEQ)
        var price_highconfig = user("rowan").getUserConfigType(IndicatorType.PRICE_HIGH)
        var chopma = user("rowan").getUserConfigType(IndicatorType.CHOPMA)


        var mutableList = mutableListOf<Deferred<Unit>>()

        coreDataIgniteService.reload()

        //someone working on this , dont run again .
        if (check()) return

        var newsconfig = JsonObject()
        newsconfig.addProperty("date", coreDataIgniteService.today().date.toString())


        newsIgniteService.getCache(newsconfig)


        jobRepo.saveAndFlush(
            Job.builder().date(coreDataIgniteService.today().date).message("Price import").build()
        )

        chopma?.forEach {
            var price_conseqconfigstring = it.algoValue
            var content = fnbasicUserConfigContent(it)
            var config = price_conseqconfigstring.split(",");
            content.addProperty("time", config[0])
            content.addProperty("sensitive", config[1])
            content.addProperty("percent", config[2])
            content.addProperty("ma", config[3])
            mutableList.add(addAsync(chopMAManger::loadall, content))

        }

        price_highconfig?.forEach {
            var fmaconfigstring = it.getAlgoValue()
            var content = fnbasicUserConfigContent(it)
            content.addProperty("days", fmaconfigstring)
            mutableList.add(addAsync(priceHighManager::loadall, content))
            println("----MA-----$content")
        }

        price_conseqconfig?.forEach {
            var price_conseqconfigstring = it.algoValue
            var content = fnbasicUserConfigContent(it)
            var config = price_conseqconfigstring.split(",");
            content.addProperty("days", config[0])
            content.addProperty("sensitive", config[1])
            content.addProperty("percent", config[2])
            mutableList.add(addAsync(faillDailyPriceManager::loadall, content))
        }

        fmaconfig?.forEach {
            var fmaconfigstring = it.algoValue
            var (maconfig, marunning) = fndelimeterPair(fmaconfigstring)
            var (arg1, operator, arg2) = threeElems(maconfig)

            var content = fnmapricefromconfig(it.id, operator, arg2, arg1)
            var z = marunning.split(",");
            content.addProperty("days", z[0])
            content.addProperty("sensitive", z[1])
            mutableList.add(addAsync(runningMAManger::loadall, content))
        }


        maconfig?.forEach {
            var maconfigstring = it.algoValue
            var (arg1, operator, arg2) = threeElems(maconfigstring.toString())

            var content = JsonObject()
            content.addProperty("id", it.id)
            content.addProperty("userid", "rowan")
            content.addProperty("type", "ma")
            content.addProperty("mode", "price")
            content.addProperty("operator", operator)
            content.addProperty("time", arg2)
            content.addProperty("percent", arg1)

            mutableList.add(addAsync(maManager::loadall, content))
        }

        var rsistring = user("rowan").getUserConfigType(IndicatorType.RSI)[0].algoValue
        var falldailystring = user("rowan").getUserConfigType(IndicatorType.PRICE_FALL)[0].algoValue
        var volumexstring = user("rowan").getUserConfigType(IndicatorType.VOLUME)[0].algoValue

        var (arg1, operator, arg2) = threeElems(rsistring!!)

        var content = JsonObject()
        content.addProperty("id", user("rowan").getUserConfigType(IndicatorType.RSI)[0].id)
        content.addProperty("userid", "rowan")
        content.addProperty("rsialgo", arg1)
        content.addProperty("type", "rsi")
        content.addProperty("operator", operator)
        content.addProperty("time", arg2)
        mutableList.add(addAsync(rSIManager::loadall, content))

        var (arg3, operator1, arg4) = threeElems(volumexstring!!)
        content = JsonObject()
        content.addProperty("id", user("rowan").getUserConfigType(IndicatorType.VOLUME)[0].id)
        content.addProperty("userid", "rowan")
        content.addProperty("operator", operator1)
        content.addProperty("type", "volumex")
        content.addProperty("time", arg4)
        content.addProperty("volumex", arg3)
        mutableList.add(addAsync(volumeManager::loadall, content))

        var (arg5, arg6) = twoElems(falldailystring!!)
        content = JsonObject()
        content.addProperty("id", user("rowan").getUserConfigType(IndicatorType.PRICE_FALL)[0].id)
        content.addProperty("userid", "rowan")
        content.addProperty("type", "price")
        content.addProperty("operator", arg6)
        content.addProperty("price", arg5)
        mutableList.add(addAsync(priceManager::loadall, content))

        runBlocking {
            mutableList.forEach {
                it.await()
            }
        }
    }

    private fun addAsync(fn: (JsonObject) -> Unit, content: JsonObject): Deferred<Unit> {
        return GlobalScope.async {
            fn(content)
        }
    }

    private fun user(username: String): User = userRepo.findOne(QUser.user.username.eq(username)).get()
}

