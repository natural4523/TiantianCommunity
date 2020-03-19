package cn.edu.sdju.soft.community.service;

import cn.edu.sdju.soft.community.mapper.CheckQuestionsExtMapper;
import cn.edu.sdju.soft.community.mapper.UserCheckQuestionExtMapper;
import cn.edu.sdju.soft.community.model.CheckQuestions;
import cn.edu.sdju.soft.community.model.UserCheckQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCheckQuestionService {

    @Autowired
    private UserCheckQuestionExtMapper userCheckQuestionExtMapper;

    @Autowired
    private CheckQuestionsExtMapper checkQuestionsExtMapper;

    public void createUserCheckQuestion(UserCheckQuestion userCheckQuestion) {
        userCheckQuestionExtMapper.createUserCheckQuestion(userCheckQuestion);
    }

    public List<UserCheckQuestion> findCheckQuestionByUserId(Long id) {
        List<UserCheckQuestion> userCheckQuestionList = userCheckQuestionExtMapper.findCheckQuestionByUserId(id);
        for (UserCheckQuestion checkQuestion:userCheckQuestionList) {
            CheckQuestions question = checkQuestionsExtMapper.findCheckQuestionByUserId(checkQuestion.getQuestion());
            checkQuestion.setCheckQuestions(question);
        }

        return userCheckQuestionList;
    }


    public UserCheckQuestion findCheckQuestion(Long question, String answer, Long id) {
        UserCheckQuestion userCheckQuestion = userCheckQuestionExtMapper.findCheckQuestion(question,answer,id);
        return userCheckQuestion;
    }
}
