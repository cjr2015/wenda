package com.nowcoder.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;


    @RequestMapping(path = {"/question/add"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestiob(Model model, @RequestParam("title") String title, @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCommentCount(0);
            question.setCreatedDate(new Date());
            if (hostHolder.getUser() == null) {
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            } else {
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
//                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
//                        .setActorId(question.getUserId()).setEntityId(question.getId())
//                        .setExt("title", question.getTitle()).setExt("content", question.getContent()));
                return WendaUtil.getJSONString(0);

            }
        } catch (Exception e) {
            logger.error("增加题目失败" + e.getMessage());
        }
        return WendaUtil.getJSONString(1, "失败");
    }

    @RequestMapping(path = {"/question/{qid}"}, method = {RequestMethod.GET})
    public String questionDetail(@PathVariable("qid") int qid, Model model) {
        Question question = questionService.getQuestionById(qid);
        User owner = userService.getUser(question.getUserId());
        model.addAttribute("question", question);
        model.addAttribute("owner",owner);
        List<Comment> commentList = commentService.selectByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment", comment);
            if (hostHolder.getUser() == null) {
                vo.set("liked", 0);
            } else {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }

            vo.set("user", userService.getUser(comment.getUserId()));
            long count = likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId());
            vo.set("likeCount", count);
            vos.add(vo);
        }


        model.addAttribute("comments", vos);
        List<ViewObject> followUsers = new ArrayList<ViewObject>();
        // 获取关注的用户信息
        List<Integer> users = followService.getFollowers(EntityType.ENTITY_QUESTION, qid, 20);
        for (Integer userId : users) {
            ViewObject vo = new ViewObject();
            User u = userService.getUser(userId);
            if (u == null) {
                continue;
            }
            vo.set("name", u.getName());
            vo.set("headUrl", u.getHeadUrl());
            vo.set("id", u.getId());
            followUsers.add(vo);
        }
        model.addAttribute("followUsers", followUsers);
        if (hostHolder.getUser() != null) {
            model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, qid));
        } else {
            model.addAttribute("followed", false);
        }

        return "detail";

    }
    @RequestMapping(path = {"/questionCount"}, method = {RequestMethod.GET})
    @ResponseBody
    public PageInfo questionCount() {
        int questionCount = questionService.getQuestionCount();
        PageInfo<Question> pageInfo = new PageInfo<>();
        pageInfo.setSumInfoCount(questionCount);
        pageInfo.setPageCount();
        return pageInfo;
    }

    @RequestMapping(path = {"/questionPage"}, method = {RequestMethod.GET})

    public String questionPage(Model model, @RequestParam(value = "offset") Integer offset) {
        List<Question> questionList = questionService.getLatestQuestions(0, 10 * (offset - 1), 10);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
            }

        model.addAttribute("vos", vos);
        model.addAttribute("curPageNum",offset);
         return "index";
    }

}

