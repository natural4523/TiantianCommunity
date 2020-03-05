package cn.edu.sdju.soft.community.service;

import cn.edu.sdju.soft.community.mapper.CheckQuestionsExtMapper;
import cn.edu.sdju.soft.community.model.CheckQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckQuestionsService {
    @Autowired
    private CheckQuestionsExtMapper checkQuestionsExtMapper;

    public List<CheckQuestions> findPartCheckQuestions1() {
        List<CheckQuestions> checkQuestionsList = checkQuestionsExtMapper.findPartCheckQuestions1();
        return checkQuestionsList;
    }

    public List<CheckQuestions> findPartCheckQuestions2() {
        List<CheckQuestions> checkQuestionsList = checkQuestionsExtMapper.findPartCheckQuestions2();
        return checkQuestionsList;
    }

    public List<CheckQuestions> findPartCheckQuestions3() {
        List<CheckQuestions> checkQuestionsList = checkQuestionsExtMapper.findPartCheckQuestions3();
        return checkQuestionsList;
    }
}
