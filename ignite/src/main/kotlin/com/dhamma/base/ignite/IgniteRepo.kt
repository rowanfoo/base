package com.dhamma.base.ignite

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.FieldsQueryCursor
import org.apache.ignite.cache.query.QueryCursor
import org.apache.ignite.cache.query.SqlFieldsQuery
import org.apache.ignite.cache.query.SqlQuery
import org.apache.ignite.configuration.CacheConfiguration
import javax.cache.Cache

////@Component
//data class IgniteRepo<T>(var ignite: Ignite, var classtype: KClass<*>) {
//    //    @Autowired
////    lateinit var ignite: Ignite
//    lateinit var cache: IgniteCache<String, T>
//
//
//    init {
//        println("-------------------kot  class name---------------${classtype.simpleName}")
//        val cacheConfiguration: CacheConfiguration<String, T> = CacheConfiguration(classtype.simpleName)
//        // cacheConfiguration.backups(1)
//        cacheConfiguration.setBackups(1)
//        cacheConfiguration.setSqlIndexMaxInlineSize(16)
//        cacheConfiguration.setIndexedTypes(String::class.java, classtype::class.java)
//
//
//        cache = ignite.getOrCreateCache(cacheConfiguration)
//        println("-------------------cache---------------${cache.name}")
//
//    }
//
//
//    fun save(key: String, value: T) {
//        cache.put(key, value)
//
//    }
//
//    fun get(key: String): T = cache.get(key)
//
//
//    fun size(): Int = cache.size()
//
//    fun values(sql_query: String, params: Array<String>): List<T> {
//        println("-------------size----------$sql_query--")
//        val query: SqlQuery<String, T> = SqlQuery(cache.name, sql_query)
//        println("-------------size----------${params.count()}--")
//        // query.setArgs("BHP.AX", "2019-07-19")
//        query.setArgs(*params)
//
//        var resultCursor: QueryCursor<Cache.Entry<String, T>> = cache.query(query)
//        return resultCursor.map { it.value }.toList()
//
//    }
//
//
//    fun fields(sql_query: String, params: Array<String>) {
//        println("-------------fields----------$sql_query--")
//        val query = SqlFieldsQuery(sql_query)
//        println("-------------fields----------${params.count()}--")
//        // query.setArgs("BHP.AX", "2019-07-19")
//        query.setArgs(*params)
//
////        var resultCursor: QueryCursor<Cache.Entry<String, T>> = cache.query(query)
//        var result = cache.query(query);
////        return result.map { it.value }.toList()
//
//        var list = mutableListOf<String>()
////        with(result) {
////            forEach( )
////        }
//        println("-------------DONE-----------")
//        //  result.forEach { print("-----close-----$it") }
//        result.forEach {
//            //            print("-----close-----$it")
//            it.forEach { print("-----params-----$it") }
//        }
//
//    }
//
//
//}


//@Component
data class IgniteRepo<T>(var ignite: Ignite, var classtype: Any) {
    //    @Autowired
//    lateinit var ignite: Ignite
    lateinit var cache: IgniteCache<String, T>


    init {
//        println("-------------------kot  class name---------------${classtype.simpleName}")
//        val cacheConfiguration: CacheConfiguration<String, T> = CacheConfiguration(classtype.simpleName)
        val cacheConfiguration: CacheConfiguration<String, T> = CacheConfiguration(classtype::class.java.simpleName)

        // cacheConfiguration.backups(1)
        cacheConfiguration.setBackups(1)
        cacheConfiguration.setSqlIndexMaxInlineSize(16)
//    cacheConfiguration.setIndexedTypes(String::class.java, T().class() )
//        cacheConfiguration.setIndexedTypes(String::class.java, classtype::class.java)

//        cacheConfiguration.setIndexedTypes(String::class.java, CoreData::class.java)
        cacheConfiguration.setIndexedTypes(String::class.java, classtype::class.java)

        cache = ignite.getOrCreateCache(cacheConfiguration)
        println("-------------------cache---------------${cache.name}")

    }

    fun get(key: String): T = cache.get(key)

    fun keys(): List<String> {
        var mykeys = mutableListOf("")
        cache.iterator().forEach { mykeys.add(it.key) }
        return mykeys
    }


    fun save(key: String, value: T) {
        cache.put(key, value)
    }

    fun values(sql_query: String, params: Array<String>): List<T> {
        //  println("-------------size----------$sql_query--")
        val query: SqlQuery<String, T> = SqlQuery(cache.name, sql_query)
//        println("-------------size----------${params.count()}--")
        // query.setArgs("BHP.AX", "2019-07-19")
        query.setArgs(*params)

        var resultCursor: QueryCursor<Cache.Entry<String, T>> = cache.query(query)
        return resultCursor.map { it.value }.toList()

    }


    fun fields(sql_query: String, params: Array<String>): FieldsQueryCursor<List<*>> {
        println("-------------fields----------$sql_query--")
        val query = SqlFieldsQuery(sql_query)
        println("-------------fields----------${params.count()}--")
        // query.setArgs("BHP.AX", "2019-07-19")
        query.setArgs(*params)

//        var resultCursor: QueryCursor<Cache.Entry<String, T>> = cache.query(query)
        var result = cache.query(query)
//        return result.map { it.value }.toList()

        var list = mutableListOf<String>()
//        with(result) {
//            forEach( )
//        }
        println("-------------DONE-----------")
        return result
        //  result.forEach { print("-----close-----$it") }
//        result.forEach {
//            //            print("-----close-----$it")
//            it.forEach { print("-----params-----$it") }
//        }

    }

    //    fun getCache(name:String) : {
//
//    }
//
    fun size(): Int = cache.size()

}