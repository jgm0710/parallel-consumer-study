package com.example.parallelconsumerstudy.sample

import com.example.parallelconsumerstudy.annotation.KafkaParallelListener
import io.confluent.parallelconsumer.PollContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.log

@Component
class SampleKafkaConsumer2 {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaParallelListener(
        topics = ["hello-topic"],
    )
    fun sample(recode: PollContext<String, String>) {
        log.info("recode : $recode. value : ${recode.value()}")
    }
}
