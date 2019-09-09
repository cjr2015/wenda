package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName, @RequestParam("content") String content) {

        try{
            if(hostHolder.getUser() == null){
                return WendaUtil.getJSONString(1,"未登录");
            }
            User user = userService.selectByName(toName);
            if (user == null) {
                return WendaUtil.getJSONString(1, "用户不存在");
            }
            Message message = new Message();
            message.setToId(user.getId());
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setHasRead(0);
            message.setFromId(hostHolder.getUser().getId());

            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);

        }catch (Exception e){
            logger.error("添加私信失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"添加私信失败");
        }

    }
    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public  String msglist(Model model){
        try {
            int localUserId = hostHolder.getUser().getId();
            List<Message> msglist = messageService.getConversationList(localUserId,0,10);

            List<ViewObject> msgs = new ArrayList<>();
            for(Message msg:msglist){
                ViewObject vo = new ViewObject();
                vo.set("message",msg);
                int unread = messageService.getConvesationUnreadCount(localUserId,msg.getConversationId());
                int targetId = msg.getFromId()==localUserId? msg.getToId():msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("unread",unread);
                vo.set("user",user);
                msgs.add(vo);
            }
            model.addAttribute("conversations",msgs);
        }catch (Exception e){
            logger.error("获取私信列表失败"+e.getMessage());
        }
        return "letter";
    }
    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public  String msgdetail(Model model,@RequestParam("conversationId") String conversationId){
        try {
            List<Message> conversationList = messageService.selectConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<>();
            for(Message message:conversationList){
                ViewObject vo = new ViewObject();
                message.setHasRead(1);
                int targetId=hostHolder.getUser().getId();
                if(message.getToId()==targetId) {
                    messageService.updateMessageHasRead(message.getConversationId(), 1, message.getToId());
                }
                vo.set("message",message);
                User user = userService.getUser(message.getFromId());
                if(user==null)
                    continue;
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages",messages);

        }catch (Exception e){
            logger.error("获取私信详情失败"+e.getMessage());
        }
        return "letterDetail";
    }
}
