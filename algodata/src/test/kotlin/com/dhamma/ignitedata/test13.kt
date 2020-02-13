package com.dhamma.ignitedata

import com.dhamma.pesistence.entity.repo.DataRepo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest
class test13 {


    @Autowired
    lateinit var dataRepo: DataRepo

    @Test
    fun getMetaData() {
//        dataRepo.findbyCodeOffset()

        for (i in 0.rangeTo(4)) {

            var no = 14 * i
            println("--------$no--------")

        }


    }


}