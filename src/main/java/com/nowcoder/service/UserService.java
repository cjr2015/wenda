package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.*;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginTicketDAO loginTicketDAO;
    @Autowired
    JedisAdapter jedisAdapter;
    public  Map<String,Object> setVerCode(String username,String mail){
        Map<String,Object> map = new HashMap<>();
        User user = userDAO.selectByMail(mail);
        if (user != null){
            map.put("msg","该邮箱已被其他用户注册");
            return map;
        }
        String verkey = RedisKeyUtil.getVerificationKey(username,mail);

        int verCode = new Random().nextInt(1000000);
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.set(verkey,""+verCode);
        // 当前用户对这类实体关注+1
        tx.expire(verkey,300);
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        map.put("username", username);
        map.put("verCode",""+verCode);

        return (ret.size() == 2 && (ret.get(0) == "OK") && (Long) ret.get(1) > 0) ? map:null ;
    }
    public String getVerCode(String username,String mail){
        String verkey = RedisKeyUtil.getVerificationKey(username,mail);
        return jedisAdapter.get(verkey);
    }
    public User getUser(int id) {
        return userDAO.selectById(id);
    }
    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }
    public Map<String,String> register(String username,String password,String mail){

        Map<String,String> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","用户名不能为空");
            return map;
        }
        User user =  userDAO.selectByName(username);
        if (user != null){
            map.put("msg","用户已存在");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        user.setMail(mail);
        user.setOnline(0);
        userDAO.addUser(user);
        String ticket = addTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }
    public Map<String,String> login(String username,String password){

        Map<String,String> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","用户名不能为空");
            return map;
        }
        User user =  userDAO.selectByName(username);
        if (user == null){
            map.put("msg","用户不存在");
            return map;
        }
        userDAO.updateOnline(1,user.getId());
         String ticket = addTicket(user.getId());
         map.put("ticket",ticket);

        return map;
    }
    public void logout(String ticker,int userId){
        userDAO.updateOnline(0,userId);
        loginTicketDAO.updateStatus(ticker,1);
    }
    private String addTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        Date date = new Date();
        date.setTime(3600*24*100+date.getTime());
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

}
