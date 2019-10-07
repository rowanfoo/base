package com.dhamma.base.ignite.util


//@Component
//class IgniteCacheConcurency {
//    lateinit var ignite: Ignite
//
////    fun getCache<X, T>(name: String, myfunc: (IgniteCache<X, T>) -> Unit): IgniteCache<X, T> {
////        var cache = ignite.getOrCreateCache<X, T>(name)
////        var lock = cache.lock("mylock")
////        try {
////            lock.lock()
////            if (cache.size() > 0) {
////                lock.unlock()
////                return cache
////            }
////
////            myfunc(cache)
////            lock.unlock()
////            return cache
////        } finally {
////            lock.unlock()
////        }
////    }
//
//
//}