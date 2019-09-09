package com.nowcoder.rabbitmq.listener;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.*;
import com.nowcoder.rabbitmq.EventModel;
import com.nowcoder.rabbitmq.EventType;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import com.nowcoder.util.WendaUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FeedMqListener {

    @Autowired
    FollowService followService;

    @Autowired
    UserService userService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdapter jedisAdapter;

    @Autowired
    QuestionService questionService;
    @RabbitListener(queues = "${feed.queue.name}",containerFactory = "singleListenerContainer")
    public void consumeLetterQueue(@Payload JSONObject jsonObject) {
        EventModel model = WendaUtil.convertJSONToObject(jsonObject);
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));
        if (feed.getData() == null) {
            // 不支持的feed
            return;
        }

        feedService.addFeed(feed);

        // 获得所有粉丝
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);

        // 给所有粉丝推事件
        for (int follower : followers) {
            int onlineStatus = userService.getUser(follower).getOnline();
            if(onlineStatus !=0) {
                String timelineKey = RedisKeyUtil.getTimelineKey(follower);
                jedisAdapter.lpush(timelineKey, String.valueOf(feed.getId()));
                // 限制最长长度，如果timelineKey的长度过大，就删除后面的新鲜事
            }
        }
        System.out.println("收到收到"+jsonObject.toJSONString());
    }
    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<String ,String>();
        // 触发用户是通用的
        User actor = userService.getUser(model.getActorId());
        if (actor == null) {
            return null;
        }
        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if (model.getType() == EventType.COMMENT ||
                (model.getType() == EventType.FOLLOW  && model.getEntityType() == EntityType.ENTITY_QUESTION)) {
            Question question = questionService.getQuestionById(model.getEntityId());
            if (question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }
}
