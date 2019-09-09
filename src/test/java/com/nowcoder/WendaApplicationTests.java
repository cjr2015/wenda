package com.nowcoder;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.Question;
import com.nowcoder.util.JedisAdapter;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigInteger;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@WebAppConfiguration
public class WendaApplicationTests {
	@Autowired
    LoginTicketDAO loginTicketDAO;

	@Autowired
	UserDAO userDAO;
	@Autowired
	QuestionDAO questionDAO;
	@Test
	public void contextLoads()  {
//
//		Random random = new Random();
//		for (int i = 0; i < 1000; ++i) {
//
//			Question question = new Question();
//			question.setCommentCount(i);
//			Date date = new Date();
//			date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
//			question.setCreatedDate(date);
//			question.setUserId(1);
//			question.setTitle(String.format("TITLE{%d}", i));
//			question.setContent(String.format("Balaababalalalal Content %d", i));
//			questionDAO.addQuestion(question);
//		questionDAO.selectLatestQuestions(1,0,10).forEach(System.out::println);
//		String NumChars = "123abcABC小喇叭";
//		char[]  a =NumChars.toCharArray();
//		for(int i=0;i<=a.length-1;i++){
//			System.out.println((char)65537);
//		}
		BigInteger b=new BigInteger("10");//1010
		System.out.println(b.toString(2));

	}
	public static String covert(int num){

		String NumChars = "123abcABC小喇叭";
		char[]  a =NumChars.toCharArray();

		return "";
	}

}
