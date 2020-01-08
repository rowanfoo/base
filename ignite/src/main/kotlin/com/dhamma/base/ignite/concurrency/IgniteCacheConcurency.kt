package com.dhamma.base.ignite.concurrency

import com.google.gson.JsonObject
import org.apache.ignite.Ignite
import org.apache.ignite.IgniteCache
import org.apache.ignite.cache.CacheAtomicityMode
import org.apache.ignite.configuration.CacheConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/*
Cannot just have a simple synhronize , because app might run multiple copy , therefore coredata cache could be populate double
using distributed locks
 */
@Component
class IgniteCacheConcurency {
    @Autowired
    lateinit var ignite: Ignite
   /*
    this is to prevent 2 thread simultaneous calling the process method , both thread in here
     */
    fun process(name: String, obj: JsonObject, callback: (JsonObject) -> Unit) {
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

            if (lockcache.get("run") > 0) {//detect 2nd thread , count >1 if 1st thread run , and 2nd thread come in just exit
                lockcache.put("run", 0)
                return

            }else{
                lockcache.put("run", 1)
                callback(obj)
            }
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