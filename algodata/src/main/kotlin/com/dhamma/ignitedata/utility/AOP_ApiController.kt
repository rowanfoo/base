package com.dhamma.ignitedata.utility

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration


@Aspect
@Configuration
class AOP_ApiController {
    @Autowired
    lateinit var email: Email

    @AfterThrowing(pointcut = "(execution(*  com.dhamma.ignitedata.manager.*.*(..)))", throwing = "e")
    fun logError(joinPoint: JoinPoint, e: Exception) {
        println("ERRRRRRRRRRRRRR-1------------ -> $joinPoint")
        println("ERRRRRRRRRRRRRR---2---------- -> $e")
        println("ERRRRRRRRRRRRRR---2---------- -> ${e.stackTrace}")

//        println("ERRRRRRRRRRRRRR---2---------- -> ${
//            e.stackTrace.}")

        var s = e.getStackTrace().toList().drop(0).map(java.lang.StackTraceElement::toString).reduce { s1, s2 -> s1.toString() + "\n" + s2 }
//        var s = "joinPoint :${joinPoint.toString()}  \n";
//        for (elem in e.stackTrace) {
//            s = s + elem.toString() + "\n"
//        }
        println("ERRRRRRRRRRRRRR---e---------- -> $s")
        email.sendSimpleMessage("joinPoint :${joinPoint.toString()}  \n  $s")
    }

}