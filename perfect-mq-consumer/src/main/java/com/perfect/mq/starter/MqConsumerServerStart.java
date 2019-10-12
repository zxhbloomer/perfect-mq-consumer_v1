package com.perfect.mq.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zxh
 */
@SpringBootApplication(
    exclude = { DataSourceAutoConfiguration.class },
    scanBasePackages = {
            "com.perfect.framework",
            "com.perfect.*",
            "com.perfect.redis",
        })
@EnableTransactionManagement
@EntityScan(basePackages = {"com.perfect.*"})
@Slf4j
@EnableCaching
public class MqConsumerServerStart {
    public static void main(String[] args) {
        log.info("-----------------------启动开始-------------------------");
        SpringApplication.run(MqConsumerServerStart.class, args);
        log.info("-----------------------启动完毕-------------------------");
    }
}
