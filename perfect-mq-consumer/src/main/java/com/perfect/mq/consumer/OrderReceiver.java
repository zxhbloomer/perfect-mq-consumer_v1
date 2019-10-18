package com.perfect.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.perfect.bean.entity.log.mq.SLogMqEntity;
import com.perfect.bean.pojo.mqsender.MqSenderPojo;
import com.perfect.common.utils.string.convert.Convert;
import com.perfect.core.service.log.mq.ISLogMqService;
import com.perfect.mq.rabbitmq.mqenum.MQEnum;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 *
 */
@Component
@Slf4j
public class OrderReceiver {

    @Autowired
    ISLogMqService service;

    /**
     * 配置监听的哪一个队列，同时在没有queue和exchange的情况下会去创建并建立绑定关系
    * @param messageDataObject
    * @param headers
    * @param channel
    * @throws IOException
     */
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = MQEnum.MqInfo.Log.queueCode, durable = "true"),
            exchange = @Exchange(name=MQEnum.MqInfo.Log.exchange, durable = "true", type = "topic"),
            key = MQEnum.MqInfo.Log.routing_key
        )
    )
    /**
     * 如果有消息过来，在消费的时候调用这个方法
     */
    @RabbitHandler
    public void onOrderMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel)
        throws IOException {
        String messageData = Convert.str(messageDataObject.getBody(), (Charset)null);
        MqSenderPojo mqSenderPojo = JSONObject.parseObject(messageData, MqSenderPojo.class);

        /**
         * 更新处理
         */
        mqOkUpdateService(mqSenderPojo.getKey());
        /**
         * 处理回调
         */
//        if(null != mqSenderPojo.getCallBackInfo()){
//            CallInfoReflectionPojo callback = mqSenderPojo.getCallBackInfo();
//            ReflectionUtil.invokeByObject(callback);
//        }

        /**
         * Delivery Tag 用来标识信道中投递的消息。RabbitMQ 推送消息给 Consumer 时，会附带一个 Delivery Tag，
         * 以便 Consumer 可以在消息确认时告诉 RabbitMQ 到底是哪条消息被确认了。
         * RabbitMQ 保证在每个信道中，每条消息的 Delivery Tag 从 1 开始递增。
         */
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        /**
         *  multiple 取值为 false 时，表示通知 RabbitMQ 当前消息被确认
         *  如果为 true，则额外将比第一个参数指定的 delivery tag 小的消息一并确认
         */
        boolean multiple = false;

        //ACK,确认一条消息已经被消费
        channel.basicAck(deliveryTag, multiple);
    }

    /**
     * 根据key获取数据
     * @param key
     * @return
     */
    private SLogMqEntity getMqData(String key){
        SLogMqEntity sLogMqEntity = service.selectByKey(key);
        return sLogMqEntity;
    }

    /**
     *
     * @param key
     */
    private void mqOkUpdateService(String key){
        SLogMqEntity sLogMqEntity = getMqData(key);
        sLogMqEntity.setConsumer_status(true);
        service.update(sLogMqEntity);
    }
}
