package com.example.parallelconsumerstudy.sample

import com.example.parallelconsumerstudy.annotation.KafkaParallelListener
import io.confluent.parallelconsumer.ParallelConsumerOptions
import io.confluent.parallelconsumer.PollContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SampleKafkaConsumer2 {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaParallelListener(
        topics = ["hello-topic"],
        concurrency = 10,
        ordering = ParallelConsumerOptions.ProcessingOrder.KEY,
        groupId = "test-group",
    )
    fun sample(recode: PollContext<String, String>) {
        log.info("recode : $recode. value : ${recode.value()}")
        Thread.sleep(10000)
    }
}
