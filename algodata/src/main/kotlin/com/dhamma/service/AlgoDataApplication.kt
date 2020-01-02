package com.dhamma.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.dhamma.base.ignite", "com.dhamma.pesistence", "com.dhamma.service"])
class AlgoDataApplication

fun main(args: Array<String>) {
    runApplication<AlgoDataApplication>(*args)
}
