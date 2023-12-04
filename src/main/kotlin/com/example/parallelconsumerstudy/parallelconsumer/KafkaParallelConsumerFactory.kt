package com.example.parallelconsumerstudy.parallelconsumer

import io.confluent.parallelconsumer.ParallelConsumerOptions
import io.confluent.parallelconsumer.ParallelStreamProcessor
import org.springframework.kafka.core.ConsumerFactory

class KafkaParallelConsumerFactory<K, V> {

    fun createConsumerProcessor(
        kafkaConsumerFactory: ConsumerFactory<K, V>,
        topics: Array<String>,
        ordering: ParallelConsumerOptions.ProcessingOrder = ParallelConsumerOptions.ProcessingOrder.KEY,
        maxConcurrency: Int = 3,
        groupId: String,
        clientIdPrefix: String? = null,
        clientIdSuffix: String? = null,
    ): ParallelStreamProcessor<K, V> {
        val options: ParallelConsumerOptions<K, V> = ParallelConsumerOptions.builder<K, V>()
            .ordering(ordering)
            .maxConcurrency(maxConcurrency)
            .consumer(
                kafkaConsumerFactory.createConsumer(
                    groupId.ifEmpty { null },
                    clientIdPrefix,
                    clientIdSuffix,
                ),
            )
            .build()

        val eosStreamProcessor: ParallelStreamProcessor<K, V> =
            ParallelStreamProcessor.createEosStreamProcessor(options)

        eosStreamProcessor.subscribe(topics.toList())

        return eosStreamProcessor
    }
}
