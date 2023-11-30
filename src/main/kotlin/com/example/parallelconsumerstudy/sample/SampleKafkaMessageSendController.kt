package com.example.parallelconsumerstudy.sample

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.math.absoluteValue
import kotlin.random.Random

@RestController
class SampleKafkaMessageSendController(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/kafka/domain-events/publish")
    fun publishKafkaEvents(
        @RequestBody request: SendKafkaMessageRequest,
    ) {
        val topic = request.topic
        val message = request.message
        val publishCount = request.publishCount
        val key = request.key

        log.info(
            "Send kafka domain events. topic : [{}], message : [{}], publishCount : [{}]",
            topic,
            message,
            publishCount,
        )

        val startTime = System.currentTimeMillis()

        repeat(publishCount) {
            kafkaTemplate.send(topic, key ?: Random.nextInt().absoluteValue.toString(), message)
        }

        val endTime = System.currentTimeMillis()

        log.info("End publish domain events. processed time : [{}]ms", endTime - startTime)
    }
}

data class SendKafkaMessageRequest(
    val publishCount: Int,
    val topic: String,
    val message: String,
    val key: String?,
)
