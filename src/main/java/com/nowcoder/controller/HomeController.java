package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nowcoder on 2016/7/15.
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    FollowService followService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getQuestions(0, 0,10));
        int questionCount = questionService.getQuestionCount();
        int pageCount = questionCount % 10 == 0 ? questionCount / 10 : (questionCount / 10) + 1;
        model.addAttribute("pageCount",pageCount);
        model.addAttribute("curPageNum",1);
        return "index";
    }



    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        if(hostHolder.getUser().getId()!=userId){
            vo.set("isCurUser",false);
        }else {
            vo.set("isCurUser",true);
        }
        model.addAttribute("profileUser", vo);
        model.addAttribute("curPageNum",1);
        return "profile";
    }
    @RequestMapping(path = {"/user/{userId}/count"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public PageInfo userIndexCount() {
        int userId = hostHolder.getUser().getId();
        int questionCount = questionService.getQuestionCountById(userId);
        PageInfo<Question> pageInfo = new PageInfo<>();
        pageInfo.setSumInfoCount(questionCount);
        pageInfo.setPageCount();
        return pageInfo;

    }
    @RequestMapping(path = {"/user/{userId}/page"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndexPage(Model model, @PathVariable("userId") int userId,@RequestParam("offset") int offset) {
        model.addAttribute("vos", getQuestions(userId, 10 * (offset - 1), 10));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
        if (hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        } else {
            vo.set("followed", false);
        }
        if(hostHolder.getUser().getId()!=userId){
            vo.set("isCurUser",false);
        }else {
            vo.set("isCurUser",true);
        }
        model.addAttribute("profileUser", vo);
        model.addAttribute("curPageNum",offset);
        return "profile";
    }
}
