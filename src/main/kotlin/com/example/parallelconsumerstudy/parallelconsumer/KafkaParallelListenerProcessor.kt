package com.example.parallelconsumerstudy.parallelconsumer

import io.confluent.parallelconsumer.ParallelStreamProcessor
import io.confluent.parallelconsumer.PollContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.stereotype.Component
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@Component
class KafkaParallelListenerProcessor(
    private val kafkaConsumerFactory: ConsumerFactory<String, String>,
) : BeanPostProcessor, DisposableBean {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val kafkaParallelConsumerFactory: KafkaParallelConsumerFactory<String, String> =
        KafkaParallelConsumerFactory()

    private val consumers = mutableListOf<ParallelStreamProcessor<String, String>>()

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        bean.javaClass.methods.forEach { method: Method ->
            method.getAnnotation(KafkaParallelListener::class.java)?.let { annotation ->
                processKafkaParallelListenerMethod(bean, method, annotation)
            }
        }
        return bean
    }

    private fun processKafkaParallelListenerMethod(
        bean: Any,
        method: Method,
        kafkaParallelListener: KafkaParallelListener,
    ) {
        val consumerProcessor = kafkaParallelConsumerFactory.createConsumerProcessor(
            kafkaConsumerFactory = kafkaConsumerFactory,
            topics = kafkaParallelListener.topics,
            ordering = kafkaParallelListener.ordering,
            maxConcurrency = kafkaParallelListener.concurrency,
            groupId = kafkaParallelListener.groupId,
            clientIdPrefix = kafkaParallelListener.clientIdPrefix,
            clientIdSuffix = kafkaParallelListener.clientIdSuffix,
        )

        consumerProcessor.poll { recode: PollContext<String, String> ->
            try {
                method.invoke(bean, recode)
            } catch (e: InvocationTargetException) {
                // TODO: error handler 를 추가할 수 있도록 구현 필요
                val originalThrowable: Throwable = e.targetException
                log.error("Kafka parallel consumer error occurred...", originalThrowable)
            } catch (throwable: Throwable) {
                // TODO: error handler 를 추가할 수 있도록 구현 필요
                log.error("Kafka parallel consumer error occurred...", throwable)
            }
        }

        consumers.add(consumerProcessor)
    }

    /**
     * Spring 종료 시 호출 부 정의
     *
     * - spring 종료 시 kafka consumer 로 연결된 connection 들을 close 하도록 구현
     *
     * @author jeong-gumin
     * @since 2023/11/28
     * */
    override fun destroy() {
        log.info("Kafka parallel consumers closed...")
        consumers.forEach { parallelStreamProcessor: ParallelStreamProcessor<String, String> ->
            try {
                parallelStreamProcessor.close()
            } catch (e: Exception) {
                log.error("Kafka parallel consumer close fail...", e)
            }
        }
        log.info("Kafka parallel consumers close completed...")
    }
}
