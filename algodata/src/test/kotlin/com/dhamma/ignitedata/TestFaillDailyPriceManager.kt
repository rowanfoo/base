package com.dhamma.ignitedata


import com.dhamma.ignitedata.manager.FaillDailyPriceManager
import com.dhamma.ignitedata.manager.MAManager
import com.dhamma.ignitedata.service.CoreDataIgniteService
import com.dhamma.pesistence.entity.data.*
import com.dhamma.pesistence.entity.repo.UserRepo
import com.google.gson.JsonObject
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest
class TestFaillDailyPriceManager {


    @Autowired
    lateinit var mamanager: MAManager

    @Autowired
    lateinit var userRepo: UserRepo

    @Autowired
    lateinit var coreDataIgniteService: CoreDataIgniteService

    @Autowired
    lateinit var faillDailyPriceManager: FaillDailyPriceManager


    @Test
    fun greater() {

//        var content = JsonObject()
//        content.addProperty("id", "2")
//        content.addProperty("userid", "3")
//        content.addProperty("mode", "price")
//        content.addProperty("operator", "<")
////        content.addProperty("time", "40")
//
//        content.addProperty("time", "200")
//
//        content.addProperty("percent", "0.3")
//
//        var hr = mamanager.runload(content)
//        hr.forEach {
//
//            if (it.value > 0) {
//                //   println("-----$it-------${it.value}")
//                doLogic(it.code)
//
//            }
//
//        }

        var list = coreDataIgniteService.lesserPercentlt("2020-01-22", "0") // find all down today

        var t = list.map {
            it.code
        }
                .map {

                    //var content = JsonObject()
                    // content.addProperty("code", "it")
                    var data = coreDataIgniteService.getDatabyLimit(7, it)
                    //content.addProperty("stats", nodaydown(data) as Any)

                    //content

                    Pair(it, nodaydown(data))


                }
                .filter {
                    var (count, total) = it.second
                    (count >= 5) && (total < -0.04)
                }



        println(t)
//        var x = coreDataIgniteService.getDatabyLimit(7, "WAF.AX")
//        println(x)
//        var z = x.count { coreData ->
//            coreData.changepercent < 0
//        }


    }

    fun nodaydown(data: List<CoreData>): Pair<Int, Double> {
        var count = 0
        var totaldown = 0.0
        data.forEach {
            if (it.changepercent < 0) {
                count++
                totaldown += it.changepercent
            }
        }
        return Pair(count, totaldown)
    }

    //PARAMS
//    content.addProperty("id", "2")
//    content.addProperty("userid", "3")
//    content.addProperty("mode", "price")
//    content.addProperty("operator", "<")
//    content.addProperty("time", "200")
//    content.addProperty("percent", "0.3")
//    content.addProperty("sensitive", "3")
//    content.addProperty("days", "30")

    @Test
    fun testMA() {


        var content = JsonObject()
        content.addProperty("id", "2")
        content.addProperty("userid", "3")
        content.addProperty("days", "7")
        content.addProperty("sensitive", "5")
        content.addProperty("percent", "-0.05")


        var x = faillDailyPriceManager.runload(content)

        x.forEach {
            println("-------${it.code}--------")
        }


    }

    private fun user(username: String): User = userRepo.findOne(QUser.user.username.eq(username)).get()


    @Test
    fun testuserMA() {
        var userconfig = user("rowan").userConfig
        var fmaconfig = user("rowan").getUserConfigType(IndicatorType.PRICE_CONSEQ)


        fmaconfig?.forEach {
            var fmaconfigstring = it.algoValue

            println("----conig-----$fmaconfigstring")

            var content = fnbasicUserConfigContent(it)

            var config = fmaconfigstring.split(",");
            println("----conig-----$config")
            content.addProperty("days", config[0])
            content.addProperty("sensitive", config[1])
            content.addProperty("percent", config[2])
            println("----MA-----$content")
        }


    }

    private fun fnbasicUserConfigContent(config: UserConfig): JsonObject {
        var content = JsonObject()
        content.addProperty("id", config.id)
        content.addProperty("userid", config.userid)
        content.addProperty("type", config.getType().name)
        return content
    }

}
