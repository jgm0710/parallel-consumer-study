package com.example.parallelconsumerstudy.annotation

import org.springframework.messaging.handler.annotation.MessageMapping

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MessageMapping
@MustBeDocumented
annotation class KafkaParallelListener
