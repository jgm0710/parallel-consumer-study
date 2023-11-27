package com.example.parallelconsumerstudy.annotation

import io.confluent.parallelconsumer.ParallelConsumerOptions
import io.confluent.parallelconsumer.ParallelStreamProcessor
import org.springframework.kafka.core.ConsumerFactory
import java.util.Properties

class KafkaParallelConsumerFactory<K, V> {

    fun createConsumerProcessor(
        kafkaConsumerFactory: ConsumerFactory<K, V>,
        ordering: ParallelConsumerOptions.ProcessingOrder = ParallelConsumerOptions.ProcessingOrder.KEY,
        maxConcurrency: Int = 3,
        groupId: String? = null,
        clientIdPrefix: String? = null,
        clientIdSuffix: String? = null,
        properties: Properties? = null,
    ): ParallelStreamProcessor<K, V> {
        val options = ParallelConsumerOptions.builder<K, V>()
            .ordering(ordering)
            .maxConcurrency(maxConcurrency)
            .consumer(
                kafkaConsumerFactory.createConsumer(
                    groupId,
                    clientIdPrefix,
                    clientIdSuffix,
                    properties,
                ),
            )
            .build()

        return ParallelStreamProcessor.createEosStreamProcessor(options)
    }
}
