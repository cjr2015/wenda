package com.nowcoder.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.WendaUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
@Component
public class FollowProvider {

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @Autowired
    private Environment env;

    public void send(EventModel eventModel){
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setExchange(env.getProperty("follow.exchange.name"));
        rabbitTemplate.setRoutingKey(env.getProperty("follow.routing.key.name"));

 
            this.rabbitTemplate.convertAndSend(WendaUtil.toJsonObject(eventModel));
        }


}
