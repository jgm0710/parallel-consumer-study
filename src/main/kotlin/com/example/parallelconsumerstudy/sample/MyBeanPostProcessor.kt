package com.example.parallelconsumerstudy.sample

import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component

@Component
class MyBeanPostProcessor : BeanPostProcessor {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Throws(BeansException::class)
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        // 빈이 초기화되기 전에 수행할 로직
        log.info("test BeanPostProcessor invoked. postProcessBeforeInitialization. beanName : $beanName")
        return bean
    }

    @Throws(BeansException::class)
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        // 빈이 초기화된 후에 수행할 로직
        log.info("test BeanPostProcessor invoked. postProcessAfterInitialization")
        return bean
    }
}
