package com.dhamma.base.ignite

import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.query.FieldsQueryCursor
import org.apache.ignite.cache.query.QueryCursor
import org.apache.ignite.cache.query.SqlFieldsQuery
import org.apache.ignite.cache.query.SqlQuery
import org.apache.ignite.configuration.CacheConfiguration
import javax.cache.Cache

data class IgniteRepo<T>(var ignite: Ignite, var classtype: Any) {
    lateinit var cache: IgniteCache<String, T>


    init {
        val cacheConfiguration: CacheConfiguration<String, T> = CacheConfiguration(classtype::class.java.simpleName)
        cacheConfiguration.setBackups(1)
        cacheConfiguration.setSqlIndexMaxInlineSize(16)
        cacheConfiguration.setIndexedTypes(String::class.java, classtype::class.java)
        cache = ignite.getOrCreateCache(cacheConfiguration)

    }

    fun get(key: String): T = cache.get(key)

    fun removeall() {
        cache.removeAll()
    }

    fun keys(): List<String> {
        var mykeys = mutableListOf("")
        cache.iterator().forEach { mykeys.add(it.key) }
        return mykeys
    }


    fun save(key: String, value: T) {
        cache.put(key, value)
    }

    //    @Synchronized
    fun values(sql_query: String, params: Array<String>): List<T> {
        val query: SqlQuery<String, T> = SqlQuery(cache.name, sql_query)
        query.setArgs(*params)

        var resultCursor: QueryCursor<Cache.Entry<String, T>> = cache.query(query)
        return resultCursor.map { it.value }.toList()
    }


    fun fields(sql_query: String, params: Array<String>): FieldsQueryCursor<List<*>> {
        val query = SqlFieldsQuery(sql_query)
        query.setArgs(*params)
        var result = cache.query(query)
        var list = mutableListOf<String>()
        return result
    }

    fun size(): Int = cache.size()

}
