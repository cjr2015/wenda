package com.nowcoder;

import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.User;
import com.nowcoder.rabbitmq.FollowProvider;
import com.nowcoder.service.UserService;
import com.nowcoder.util.MailSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import javax.mail.internet.MimeMultipart;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@WebAppConfiguration
public class QueueTest {

    @Autowired
    MailSender mailSender;
    @Autowired
    UserDAO userDAO;

    @Test
    public void test1(){
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("username", "abc");
//        mailSender.sendWithHTMLTemplate("2528255245@qq.com", "注册验证", "mails/login_exception.html", map);
//        int v = new Random().nextInt(1000);
//        System.out.println(v);
        User user = userDAO.selectByMail("1@163.com");
        System.out.println(user);
    }
}

