package com.dhamma.ignitedata

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.dhamma.base.ignite", "com.dhamma.pesistence", "com.dhamma.ignitedata"])
class AlgoDataApplication

fun main(args: Array<String>) {
    runApplication<AlgoDataApplication>(*args)
}
