package com.nowcoder.rabbitmq.listener;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.controller.QuestionController;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.rabbitmq.EventModel;
import com.nowcoder.rabbitmq.EventType;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FollowMqListener {
    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    @RabbitListener(queues = "${follow.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeLetterQueue(@Payload JSONObject jsonObject) {
        try {
            EventModel model = WendaUtil.convertJSONToObject(jsonObject);
            Message message = new Message();
            message.setFromId(WendaUtil.SYSTEM_USERID);
            message.setToId(model.getEntityOwnerId());
            message.setCreatedDate(new Date());
            User user = userService.getUser(model.getActorId());
            if(model.getType() == EventType.FOLLOW) {
                if (model.getEntityType() == EntityType.ENTITY_QUESTION) {
                    message.setContent("用户" + user.getName()
                            + "关注了你的问题,http://127.0.0.1:8080/question/" + model.getEntityId());
                } else if (model.getEntityType() == EntityType.ENTITY_USER) {
                    message.setContent("用户" + user.getName()
                            + "关注了你,http://127.0.0.1:8080/user/" + model.getActorId());
                }
            }else if(model.getType() == EventType.LIKE){
                message.setContent("用户" + user.getName()
                        + "赞了你的评论,http://127.0.0.1:8080/question/" + model.getExt("questionId"));

            }

            messageService.addMessage(message);
        }catch (Exception e){
            logger.error("发送站内信失败"+e.getMessage());
        }
        System.out.println("收到收到"+jsonObject.toJSONString());
    }
}
