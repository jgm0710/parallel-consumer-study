package com.example.parallelconsumerstudy

import com.example.parallelconsumerstudy.consumer.CoreApp
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class ParallelConsumerStudyApplication(
    val coreApp: CoreApp,
) {

    @Bean
    fun applicationRunner(): ApplicationRunner {
        return ApplicationRunner {
//            coreApp.run()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ParallelConsumerStudyApplication>(*args) {
    }
}
