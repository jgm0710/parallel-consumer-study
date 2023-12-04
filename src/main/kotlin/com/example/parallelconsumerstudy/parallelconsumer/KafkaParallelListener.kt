package com.example.parallelconsumerstudy.parallelconsumer

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

    /**
     * 처리 순서 지정.
     *
     * - kafka recode 처리 시 처리 순서 전략을 지정합니다.
     * - 기본값은 key 값 기준 정렬 순서 기능을 제공합니다.
     * - 자세한 내용은 [ProcessingOrder] 의 내용을 참고 부탁 드립니다.
     *
     * @author jeong-gumin
     * @since 2023/12/04
     * */
    val ordering: ProcessingOrder = ProcessingOrder.KEY, // 처리 순서

    /**
     * 동시성 설정 지정.
     *
     * - 동시에 처리할 수 있는 병렬 스레드의 개수를 지정합니다.
     * - 기본값은 3으로 설정되어 있습니다.
     * - 추가 할당 시 해당 annotation 에 지정된, kafka topic, consuemr group 기준으로 recode 를 처리하기 위한 consumer worker thread 를 할당합니다.
     * - 너무 많은 수의 concurrency 를 지정할 경우 thread pool 개수 제한을 벗어날 수 있습니다. 설정 시 운영 환경울 고려한 설정이 필요합니다.
     * - 불필요하게 많은 concurrency 설정은 thread 자원을 낭비할 수 있습니다. 운영 환경에 따라 적정 수의 concurrency 설정이 필요합니다.
     *
     * @author jeong-gumin
     * @since 2023/12/04
     * */
    val concurrency: Int = 3, // 최대 병렬 처리 수

    /**
     * Consumer group id 지정.
     *
     * - 지정한 topic 에 대한 consumer group id 를 지정합니다.
     * - 동일 topic 에 대해 다른 consumer group 을 할당하는 경우. 별도의 병렬 처리 queue 에서 recode 를 관리하게 되므로, 설정에 유의 바랍니다.
     *
     * @author jeong-gumin
     * @since 2023/12/04
     * */
    val groupId: String = "", // 그룹 ID

    /**
     * When provided, overrides the client id property in the consumer factory
     * configuration. A suffix ('-n') is added for each container instance to ensure
     * uniqueness when concurrency is used.
     * <p>SpEL {@code #{...}} and property place holders {@code ${...}} are supported.
     * @return the client id prefix.
     * @since 2.1.1
     */
    val clientIdPrefix: String = "",
)
