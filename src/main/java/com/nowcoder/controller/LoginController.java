package com.nowcoder.controller;


import com.nowcoder.model.User;
import com.nowcoder.service.UserService;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.MailSender;
import com.nowcoder.util.RedisKeyUtil;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Controller
public class LoginController {

    @Autowired
    UserService userService;
    @Autowired
    MailSender mailSender;
    @Autowired
    JedisAdapter jedisAdapter;
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @RequestMapping(path = {"/sendVer"}, method = {RequestMethod.POST})
    @ResponseBody
    public String sendVer(@RequestParam("username") String username,
                         @RequestParam("mail") String mail) {
        Map<String, Object> map =userService.setVerCode(username,mail);
        if(map.containsKey("verCode")) {
            mailSender.sendWithHTMLTemplate(mail, "注册验证码", "mails/login_exception.html", map);
            return WendaUtil.getJSONString(1);
        }
        else {
            return WendaUtil.getJSONString(0,"邮箱已被注册");
        }
    }
    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String register(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                           @RequestParam("mail") String mail,
                           @RequestParam("verification") String verification,
                           HttpServletResponse response,
                           Model model)
    {
        try {
            String verCode = userService.getVerCode(username,mail);
            if(!Objects.equals(verCode,verification)){
                model.addAttribute("msg", "验证码错误");
                return "register";
            }
            Map<String, String> map = userService.register(username, password,mail);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "register";
            }

        }catch (Exception e){
            logger.error("注册异常"+e.getMessage());
            return "register";
        }
    }
    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login( @RequestParam("username") String username,
                         @RequestParam("password") String password,
                         @RequestParam(value = "rememberme",defaultValue = "false") boolean rememberme,
                         @RequestParam(value = "next",required = false) String next,
                            HttpServletResponse response,
                            Model model)
    {
        try {
            Map<String, String> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                if(rememberme){
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
//                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
//                        .setExt("username", username).setExt("email", "1197699174@qq.com")
//                        );
                if(StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }

                return "redirect:/";
            }else {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }

        }catch (Exception e){
            logger.error("注册异常"+e.getMessage());
            return "login";
        }

        }
    @RequestMapping(path = {"/logout/{userId}"}, method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket,@PathVariable("userId") int userId){

        userService.logout(ticket,userId);
        return "redirect:/";
    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model,@RequestParam(value = "next",defaultValue = "",required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }
    @RequestMapping(path = {"/register"}, method = {RequestMethod.GET})
    public String registerPage(Model model,@RequestParam(value = "next",defaultValue = "",required = false) String next) {
        model.addAttribute("next", next);
        return "register";
    }
    @RequestMapping(path = {"/getUserHeadUrl"}, method = {RequestMethod.POST})
    @ResponseBody
    public String getUserHeadUrl(@RequestParam(value ="userName") String userName){

     User user = userService.selectByName(userName);
     return user.getHeadUrl();
    }

}
