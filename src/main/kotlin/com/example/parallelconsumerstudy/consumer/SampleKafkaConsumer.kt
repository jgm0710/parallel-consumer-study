package com.example.parallelconsumerstudy.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.stereotype.Component

@Component
class SampleKafkaConsumer(
) {

    fun sample(consumerRecord: ConsumerRecord<String, String>) {
    }
}
