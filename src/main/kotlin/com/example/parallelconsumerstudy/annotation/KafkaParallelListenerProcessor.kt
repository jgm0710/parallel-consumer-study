package com.example.parallelconsumerstudy.annotation

import io.confluent.parallelconsumer.ParallelStreamProcessor
import io.confluent.parallelconsumer.PollContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.stereotype.Component
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
        // bean의 모든 메서드를 순회하면서 KafkaParallelListener 애너테이션을 찾습니다.
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
        // KafkaParallelConsumerFactory를 사용하여 컨슈머 프로세서를 생성하고, poll 메서드에 메서드를 연결합니다.
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
            } catch (throwable: Throwable) {
                // TODO: error handler 를 추가할 수 있도록 구현 필요
                log.error("Kafka parallel consumer error occurred...", throwable)
            }
        }

        consumers.add(consumerProcessor)
        // 이 부분은 구체적인 로직에 따라 달라질 수 있습니다.
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
//                parallelStreamProcessor.close(Duration.ofMinutes(1), DrainingCloseable.DrainingMode.DRAIN)
                parallelStreamProcessor.close()
            } catch (e: Exception) {
                log.error("Kafka parallel consumer close fail...", e)
            }
        }
        log.info("Kafka parallel consumers close completed...")
    }
}
