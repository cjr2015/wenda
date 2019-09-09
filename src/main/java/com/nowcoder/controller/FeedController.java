package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;
    @RequestMapping(path = {"/pullfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getPullFeeds(Model model){

        int localUserId = hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        List<Integer> followees = new ArrayList<>();
        if(localUserId != 0){
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        List<Feed>  feeds = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }
    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getPushFeeds(Model model){
        HashMap<String,Feed> feeds = new HashMap<>();
        int localUserId = hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        if(jedisAdapter.llen(RedisKeyUtil.getTimelineKey(localUserId))!=0) {
            List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
            for (String feedId : feedIds) {
                Feed feed = feedService.getById(Integer.parseInt(feedId));
                if (feed != null) {
                    feeds.put(String.valueOf(feedId),feed);
                }
            }
        }
        List<Integer> followees = new ArrayList<>();
        if(localUserId != 0){
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER,Integer.MAX_VALUE);
        }
        List<Feed>  feedsPull = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        for(Feed feed:feedsPull) {
            if (!feeds.containsKey(String.valueOf(feed.getId()))) {
                feeds.put(String.valueOf(feed.getId()),feed);
            }
        }
         model.addAttribute("feeds", feeds);
        return "feeds";
    }
//    @RequestMapping(path = {"/pushfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
//    private String getPushFeeds(Model model) {
//        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
//        List<String> feedIds = jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
//        List<Feed> feeds = new ArrayList<Feed>();
//        for (String feedId : feedIds) {
//            Feed feed = feedService.getById(Integer.parseInt(feedId));
//            if (feed != null) {
//                feeds.add(feed);
//            }
//        }
//        model.addAttribute("feeds", feeds);
//        return "feeds";
//    }

}
