package com.dhamma.ignitedata.service


import com.dhamma.pesistence.entity.data.CoreData
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


open abstract class BaseService {
    @Autowired
    lateinit var coreDataIgniteService: CoreDataIgniteService

    fun getData(time: Int, code: String): List<CoreData> {
        return coreDataIgniteService.getDatabyLimit(time, code)
    }

    fun today() = coreDataIgniteService.today().date

    // abstract fun getResult(obj: JsonObject): List<JsonObject>


    fun compare(useralgo: JsonObject, content: JsonObject): Optional<JsonObject> {
        var operator: String = useralgo.get("operator").asString
        var percent = useralgo.get("percent").asDouble

        when {
            (operator == ">") -> {
                if (content.get("percentage").asDouble  > percent) {
                    return Optional.of(content)
                }
            }
            (operator == "<") -> {
                if (content.get("percentage").asDouble < percent) {
                    return Optional.of(content)
                }
            }
            else -> {
                if (content.get("percentage").asDouble == percent) {
                    return Optional.of(content)
                }
            }


        }
        return Optional.empty()
    }
}
