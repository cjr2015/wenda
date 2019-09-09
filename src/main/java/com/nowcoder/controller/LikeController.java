package com.nowcoder.controller;

import com.nowcoder.rabbitmq.EventModel;

import com.nowcoder.rabbitmq.EventType;
import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.rabbitmq.FollowProvider;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.xml.ws.Action;

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowProvider followProvider;
    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser()==null){
         return WendaUtil.getJSONString(999);
        }
        Comment comment = commentService.getCommentById(commentId);
        long likeCount =  likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT,commentId);
        if(likeCount!=0) {
//            eventProducer.fireEvent(new EventModel(EventType.LIKE)
//                    .setActorId(hostHolder.getUser().getId())
//                    .setEntityId(commentId).setEntityType(EntityType.ENTITY_COMMENT)
//                    .setEntityOwnerId(comment.getUserId()).setExt("questionId", String.valueOf(comment.getEntityId())));
            followProvider.send(new EventModel(EventType.LIKE)
                    .setActorId(hostHolder.getUser().getId())
                    .setEntityId(commentId).setEntityType(EntityType.ENTITY_COMMENT)
                    .setEntityOwnerId(comment.getUserId()).setExt("questionId", String.valueOf(comment.getEntityId())));
        }

        return WendaUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("commentId") int commentId) {
        if (hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
