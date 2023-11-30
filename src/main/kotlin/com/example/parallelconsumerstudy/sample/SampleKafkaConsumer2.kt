package com.example.parallelconsumerstudy.sample

import com.example.parallelconsumerstudy.annotation.KafkaParallelListener
import io.confluent.parallelconsumer.ParallelConsumerOptions
import io.confluent.parallelconsumer.PollContext
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class SampleKafkaConsumer2 {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["normal-topic"], groupId = "sample2")
    fun normalKafkaConsumer(consumerRecord: ConsumerRecord<String, String>) {
        val offset = consumerRecord.offset()
        val key = consumerRecord.key()

        log.info("Key : $key , Offset : $offset")

        val threadSleepS: Long = 1

        log.info("Thread Sleep Second : [$threadSleepS]s")

        Thread.sleep(threadSleepS * 1000)

        log.info("End Thread Sleep. Key : $key , Offset : $offset")
    }

    @KafkaParallelListener(
        topics = ["parallel-consumer-topic"],
        concurrency = 1000,
        ordering = ParallelConsumerOptions.ProcessingOrder.KEY,
        groupId = "test-group",
    )
    fun sample(recode: PollContext<String, String>) {
        invoke("sample1", recode)
    }

    @KafkaParallelListener(
        topics = ["parallel-consumer-topic"],
        concurrency = 1000,
        ordering = ParallelConsumerOptions.ProcessingOrder.KEY,
        groupId = "test-group2",
    )
    fun sample2(recode: PollContext<String, String>) {
        invoke("sample2", recode)
    }

    private fun invoke(funcName: String, recode: PollContext<String, String>) {
        log.info("funcName : $funcName, recode : $recode. value : ${recode.value()}")
        val offset = recode.offset()

        val key = recode.key()

        log.info("Key : $key, Offset : $offset")

        val l = offset % 10

        //        val threadSleepS = (10 - l) * 2
        val threadSleepS = 1L

        //        if (Random.nextInt().absoluteValue % 3 == 0) {
        //            throw Exception("test exception")
        //        }

        log.info("Thread Sleep [$threadSleepS] s")

        Thread.sleep(threadSleepS * 1000)

        log.info("End Thread Sleep of offset[$offset]. funcName : $funcName")
    }
}
