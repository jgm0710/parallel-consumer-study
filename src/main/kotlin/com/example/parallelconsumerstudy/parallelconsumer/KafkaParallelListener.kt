package com.example.parallelconsumerstudy.parallelconsumer

import io.confluent.parallelconsumer.ParallelConsumerOptions
import org.springframework.messaging.handler.annotation.MessageMapping

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MessageMapping
@MustBeDocumented
annotation class KafkaParallelListener(
    /**
     * Topic 지정
     *
     * - consumer 를 연결할 kafka topic 명 지정.
     * - 여러 토픽을 지정할 수 있습니다.
     *
     * @author jeong-gumin
     * @since 2023/12/04
     * */
    val topics: Array<String>,
    val ordering: ParallelConsumerOptions.ProcessingOrder = ParallelConsumerOptions.ProcessingOrder.KEY, // 처리 순서
    val concurrency: Int = 3, // 최대 병렬 처리 수
    val groupId: String = "", // 그룹 ID
    val clientIdPrefix: String = "", // 클라이언트 ID 접두사
    val clientIdSuffix: String = "", // 클라이언트 ID 접미사
)
