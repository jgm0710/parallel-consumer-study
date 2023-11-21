package com.example.parallelconsumerstudy

import com.example.parallelconsumerstudy.consumer.CoreApp
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import javax.annotation.PostConstruct

@EnableKafka
@SpringBootApplication
class ParallelConsumerStudyApplication(
    val coreApp: CoreApp,
) {

    @PostConstruct
    fun postConstructor() {
        coreApp.run()
    }
}

fun main(args: Array<String>) {
    runApplication<ParallelConsumerStudyApplication>(*args) {
    }
}
