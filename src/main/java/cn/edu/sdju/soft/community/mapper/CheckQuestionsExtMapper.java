package cn.edu.sdju.soft.community.mapper;

import cn.edu.sdju.soft.community.model.CheckQuestions;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CheckQuestionsExtMapper {
    List<CheckQuestions> findPartCheckQuestions1();

    List<CheckQuestions> findPartCheckQuestions2();

    List<CheckQuestions> findPartCheckQuestions3();

    CheckQuestions findCheckQuestionByUserId(@Param("id") Long id);
}
