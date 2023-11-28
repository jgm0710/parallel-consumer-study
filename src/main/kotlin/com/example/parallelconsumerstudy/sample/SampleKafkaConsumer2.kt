package com.example.parallelconsumerstudy.sample

import com.example.parallelconsumerstudy.annotation.KafkaParallelListener
import io.confluent.parallelconsumer.ParallelConsumerOptions
import io.confluent.parallelconsumer.PollContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.math.absoluteValue
import kotlin.random.Random

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
        val offset = recode.offset()

        val key = recode.key()

        log.info("Key : $key, Offset : $offset")

        val l = offset % 10

        val threadSleepS = (10 - l) * 2

        if (Random.nextInt().absoluteValue % 3 == 0) {
            throw Exception("test exception")
        }

        log.info("Thread Sleep [$threadSleepS] s")

        Thread.sleep(threadSleepS * 1000)

        log.info("End Thread Sleep of offset[$offset]")
    }
}
