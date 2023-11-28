package com.example.parallelconsumerstudy.sample

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.math.absoluteValue
import kotlin.random.Random

@RestController
class SampleKafkaMessageSendController(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    @GetMapping("/sample")
    fun sample() {
        kafkaTemplate.send("hello-topic", Random.nextInt().absoluteValue.toString(), "hihi")
    }
}
