package com.example.parallelconsumerstudy.annotation

import io.confluent.parallelconsumer.ParallelConsumerOptions
import org.springframework.messaging.handler.annotation.MessageMapping

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MessageMapping
@MustBeDocumented
annotation class KafkaParallelListener(
    val topics: Array<String>, // Kafka 토픽
    val ordering: ParallelConsumerOptions.ProcessingOrder = ParallelConsumerOptions.ProcessingOrder.KEY, // 처리 순서
    val maxConcurrency: Int = 3, // 최대 병렬 처리 수
    val groupId: String = "", // 그룹 ID
    val clientIdPrefix: String = "", // 클라이언트 ID 접두사
    val clientIdSuffix: String = "", // 클라이언트 ID 접미사
    val properties: Array<String> = [], // 추가 프로퍼티
)
