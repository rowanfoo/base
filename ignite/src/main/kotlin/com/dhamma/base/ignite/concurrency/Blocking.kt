package com.dhamma.base.ignite.concurrency

import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque


interface BQueueHandler<T> {
    fun process(a: T)
}

class BQueue<T>(var nothread: Int, var callback: BQueueHandler<T>) {
    var queue: BlockingQueue<T> = LinkedBlockingDeque()

    init {

//        val threadPool = Executors.newFixedThreadPool(1)
        val threadPool2 = Executors.newFixedThreadPool(nothread)
//        threadPool.execute {
//            while (true) {
//                println("----------msg--------${Thread.currentThread().name}")
//                var item = queue.take()
//                println("----------ITEMS--------$item")
//                threadPool2.execute {
//                    callback.process(item)
//                }
//
//
//            }
//
//        }

        Thread {
            while (true) {
                println("----------msg--------${Thread.currentThread().name}")
                var item = queue.take()
                println("----------ITEMS--------$item")
                threadPool2.execute {
                    callback.process(item)
                }


            }
        }.start()


    }


    fun add(item: T) {
        queue.put(item)
    }

    fun add(item: List<T>) {
        queue = LinkedBlockingDeque(item)
    }


}