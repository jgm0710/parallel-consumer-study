package com.example.parallelconsumerstudy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ParallelConsumerStudyApplication

fun main(args: Array<String>) {
    runApplication<ParallelConsumerStudyApplication>(*args)
}
