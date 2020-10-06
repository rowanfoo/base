package com.dhamma.ignitedata.manager

import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.ignitedata.service.NewsIgniteService
import com.dhamma.pesistence.entity.data.IndicatorType
import com.dhamma.pesistence.entity.data.Job
import com.dhamma.pesistence.entity.data.QUser
import com.dhamma.pesistence.entity.data.User
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
    lateinit var coreDataIgniteService: CoreDataIgniteService

    @Autowired
    lateinit var jobRepo: JobRepo


    @Autowired
    lateinit var newsIgniteService: NewsIgniteService


    public fun startimport() {

        var userconfig = user("rowan").userConfig
//        var maconfig = userconfig.get("ma")
        var maconfig = user("rowan").getUserConfigType(IndicatorType.MA)

        var mutableList = mutableListOf<Deferred<Unit>>()

        coreDataIgniteService.reload()
        var newsconfig = JsonObject()
        newsconfig.addProperty("date", coreDataIgniteService.today().date.toString())


        newsIgniteService.getCache(newsconfig)


        jobRepo.save(
                Job.builder().date(coreDataIgniteService.today().date).message("Price import").build()
        )

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

