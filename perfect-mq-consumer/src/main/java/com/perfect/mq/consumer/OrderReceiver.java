package com.perfect.mq.consumer;

import com.perfect.mq.rabbitmq.config.MQEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @Author：LovingLiu
 * @Description: 消费者消费
 * @Date：Created in 2019-09-24
 */
@Component
@Slf4j
@RabbitListener(queues = MQEnum.Constants.MQ_OPER_LOG)
public class OrderReceiver {

    @RabbitHandler
    public void process(String optLog) {
        log.debug("optLog");
    }
}
