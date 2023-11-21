package com.example.parallelconsumerstudy.consumer // ktlint-disable filename
/*-
 * Copyright (C) 2020-2023 Confluent, Inc.
 */
import io.confluent.csid.utils.StringUtils
import io.confluent.parallelconsumer.ParallelConsumerOptions
import io.confluent.parallelconsumer.ParallelConsumerOptions.ProcessingOrder
import io.confluent.parallelconsumer.ParallelStreamProcessor
import io.confluent.parallelconsumer.ParallelStreamProcessor.ConsumeProduceResult
import io.confluent.parallelconsumer.PollContext
import io.confluent.parallelconsumer.RecordContext
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.stereotype.Component
import pl.tlinkowski.unij.api.UniLists
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors
import kotlin.random.Random

/**
 * Basic core examples
 */
@Component
class CoreApp(
    private val kafkaConsumerFactory: DefaultKafkaConsumerFactory<String, String>,
    private val kafkaProducerFactory: DefaultKafkaProducerFactory<String, String>,
) {
    var inputTopic = "input-sample-topic"
    var outputTopic = "output-topic-" + Random.nextInt()

    var parallelConsumer: ParallelStreamProcessor<String, String>? = null

    val kafkaConsumer: Consumer<String, String> = kafkaConsumerFactory.createConsumer()
    val kafkaProducer: Producer<String, String> =
        kafkaProducerFactory.createProducer()

    fun run() {
        parallelConsumer = setupParallelConsumer()
        postSetup()

        // tag::example[]
        parallelConsumer!!.poll { record: PollContext<String, String?>? ->
            log.info(
                "Concurrently processing a record: {}",
                record,
            )
        }
        // end::example[]
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    protected fun postSetup() {
        // ignore
    }

    fun setupParallelConsumer(): ParallelStreamProcessor<String, String> {
        // tag::exampleSetup[]

        val options = ParallelConsumerOptions.builder<String, String?>()
            .ordering(ProcessingOrder.PARTITION) // <2>
            .maxConcurrency(2) // <3>
            .consumer(kafkaConsumer)
            .producer(kafkaProducer)
            .build()
        val eosStreamProcessor: ParallelStreamProcessor<String, String> =
            ParallelStreamProcessor.createEosStreamProcessor(options)
        eosStreamProcessor.subscribe(UniLists.of(inputTopic)) // <4>
        return eosStreamProcessor
        // end::exampleSetup[]
    }

    fun close() {
        parallelConsumer!!.close()
    }

    fun runPollAndProduce() {
        parallelConsumer = setupParallelConsumer()
        postSetup()

        // tag::exampleProduce[]
        parallelConsumer!!.pollAndProduce(
            { context: PollContext<String, String?> ->
                val consumerRecord =
                    context.singleRecord.consumerRecord
                val result =
                    processBrokerRecord(consumerRecord)
                ProducerRecord<String, String?>(outputTopic, consumerRecord.key(), result.payload)
            },
        ) { consumeProduceResult: ConsumeProduceResult<String, String?, String, String?> ->
            log.info(
                "Message {} saved to broker at offset {}",
                consumeProduceResult.out,
                consumeProduceResult.meta.offset(),
            )
        }
        // end::exampleProduce[]
    }

    private fun processBrokerRecord(consumerRecord: ConsumerRecord<String, String?>): Result {
        return CoreApp.Result("Some payload from " + consumerRecord.value())
    }

    @JvmInline
    value class Result(val payload: String? = null)

    fun customRetryDelay() {
        // tag::customRetryDelay[]
        val multiplier = 0.5
        val baseDelaySecond = 1
        ParallelConsumerOptions.builder<String?, String?>()
            .retryDelayProvider { recordContext: RecordContext<String?, String?> ->
                val numberOfFailedAttempts = recordContext.numberOfFailedAttempts
                val delayMillis =
                    (baseDelaySecond * Math.pow(multiplier, numberOfFailedAttempts.toDouble()) * 1000).toLong()
                Duration.ofMillis(delayMillis)
            }
        // end::customRetryDelay[]
    }

    fun maxRetries() {
        val pc = ParallelStreamProcessor.createEosStreamProcessor<String, String>(null)
        // tag::maxRetries[]
        val maxRetries = 10
        val retriesCount: MutableMap<ConsumerRecord<String, String>, Long> = ConcurrentHashMap()
        pc.poll { context: PollContext<String, String> ->
            val consumerRecord =
                context.singleRecord.consumerRecord
            val retryCount = retriesCount.computeIfAbsent(
                consumerRecord,
            ) { ignore: ConsumerRecord<String, String>? -> 0L }
            if (retryCount < maxRetries) {
                processRecord(consumerRecord)
                // no exception, so completed - remove from map
                retriesCount.remove(consumerRecord)
            } else {
                log.warn("Retry count {} exceeded max of {} for record {}", retryCount, maxRetries, consumerRecord)
                // giving up, remove from map
                retriesCount.remove(consumerRecord)
            }
        }
        // end::maxRetries[]
    }

    private fun processRecord(record: ConsumerRecord<String, String>) {
        // no-op
    }

    fun circuitBreaker() {
        val pc = ParallelStreamProcessor.createEosStreamProcessor<String, String>(null)
        // tag::circuitBreaker[]
        val upMap: MutableMap<String?, Boolean> = ConcurrentHashMap()
        pc.poll { context: PollContext<String, String> ->
            val consumerRecord =
                context.singleRecord.consumerRecord
            val serverId = extractServerId(consumerRecord)
            var up = upMap.computeIfAbsent(
                serverId,
            ) { ignore: String? -> true }
            if (!up) {
                up = updateStatusOfSever(serverId)
            }
            if (up) {
                try {
                    processRecord(consumerRecord)
                } catch (e: Exception) {
                    log.warn(
                        "Server {} is circuitBroken, will retry message when server is up. Record: {}",
                        serverId,
                        consumerRecord,
                    )
                    upMap[serverId] = false
                }
                // no exception, so set server status UP
                upMap[serverId] = true
            } else {
                throw RuntimeException(
                    StringUtils.msg(
                        "Server {} currently down, will retry record latter {}",
                        up,
                        consumerRecord,
                    ),
                )
            }
        }
        // end::circuitBreaker[]
    }

    private fun updateStatusOfSever(serverId: String?): Boolean {
        return false
    }

    private fun extractServerId(consumerRecord: ConsumerRecord<String, String>): String? {
        // no-op
        return null
    }

    fun batching() {
        // tag::batching[]
        ParallelStreamProcessor.createEosStreamProcessor(
            ParallelConsumerOptions.builder<String, String?>()
                .consumer(kafkaConsumer)
                .producer(kafkaProducer)
                .maxConcurrency(100)
                .batchSize(5) // <1>
                .build(),
        )
        parallelConsumer!!.poll { context: PollContext<String, String?> ->
            // convert the batch into the payload for our processing
            val payload = context.stream()
                .map { rc: RecordContext<String, String?> ->
                    preparePayload(
                        rc,
                    )
                }
                .collect(Collectors.toList())
            // process the entire batch payload at once
            processBatchPayload(payload)
        }
        // end::batching[]
    }

    private fun processBatchPayload(batchPayload: List<String>) {
        log.info("batchPayload : $batchPayload")
        // example
    }

    private fun preparePayload(rc: RecordContext<String, String?>): String {
        val consumerRecords = rc.consumerRecord
        val failureCount = rc.numberOfFailedAttempts
        return StringUtils.msg("{}, {}", consumerRecords, failureCount)
    }
}
