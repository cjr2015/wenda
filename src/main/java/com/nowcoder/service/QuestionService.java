package com.nowcoder.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Created by nowcoder on 2016/7/15.
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    SensitiveService sensitiveService;

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }
    public List<Question> getAllQuestions(int pageNum) {
        PageHelper.startPage(pageNum, 10);
        List<Question> list = questionDAO.getAllQuestions();
        PageInfo page = new PageInfo(list);
        return list;
    }
    public int addQuestion(Question question){
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        return questionDAO.addQuestion(question)>0? question.getId():0;
    }
    public Question getQuestionById(int id){
        return  questionDAO.selectQuestionById(id);
    }
    public int updateCommentCount( int id, int commentCount){
        return questionDAO.updateCommentCount(id,commentCount);
    }
    public int getQuestionCount(){
        return questionDAO.getQuestionCount();
    }
    public int getQuestionCountById(int id){
        return questionDAO.getQuestionCountById(id);
    }
}
