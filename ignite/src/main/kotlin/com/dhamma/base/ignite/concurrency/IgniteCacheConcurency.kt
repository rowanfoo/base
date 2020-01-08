package com.dhamma.base.ignite.concurrency

import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.configuration.CacheConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class IgniteCacheConcurency {
    @Autowired
    lateinit var ignite: Ignite

    fun process(name: String, obj: JsonObject, myfunc: (JsonObject) -> Unit) {
        println("-----start -concurrency ${Thread.currentThread().name}------------")
//        var cache = ignite.getOrCreateCache<String, Any>(name)


        //    println("------concurrency ${Thread.currentThread().name}---cache---------")

        var lockcache = getLock(name)
        var mylock = lockcache.lock("mylock")

        try {
            println("------concurrency ${Thread.currentThread().name}--------lock1----")

//            val cache = ignite.getOrCreateCache<String, Any>(name)

            mylock.lock()

            println("------concurrency ${Thread.currentThread().name}--------lock2----")
            println("------concurrency ${Thread.currentThread().name}--------lock2--size ${lockcache.size()}--")

            if (lockcache.get("run") > 0) {
                println("------concurrency ${Thread.currentThread().name}---CACHE SIZE > 0---------")

//                mylock.unlock()
                println("------concurrency ${Thread.currentThread().name}---CACHE SIZE > 0-- UNLOCK-------")
                return
                //return cache
            }
            println("------concurrency ${Thread.currentThread().name}---process---------")
            lockcache.put("run", 1)
            myfunc(obj)
//            mylock.unlock()
            println("------concurrency ${Thread.currentThread().name}---process--done-------")

            //return cache
            return
        } finally {
            println("------concurrency ${Thread.currentThread().name}---FINALLY---------")
            mylock.unlock()
        }
    }

    /*
    1, only 1 thread will process (concurrency)
    2. once run , will never run again.
     */
    private fun getLock(name: String): IgniteCache<String, Int> {
        val cc = CacheConfiguration<String, Int>("$name -1")
        cc.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
        var cache = ignite.getOrCreateCache<String, Int>(cc)
        cache.putIfAbsent("run", 0)
        //       cache.put("run", 0)
        return cache


    }

}