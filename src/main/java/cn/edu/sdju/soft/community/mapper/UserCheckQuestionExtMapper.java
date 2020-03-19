package cn.edu.sdju.soft.community.mapper;

import cn.edu.sdju.soft.community.model.UserCheckQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserCheckQuestionExtMapper {
    void createUserCheckQuestion(UserCheckQuestion userCheckQuestion);

    List<UserCheckQuestion> findCheckQuestionByUserId(@Param("id") Long id);

    UserCheckQuestion findCheckQuestion(@Param("question") Long question, @Param("answer") String answer,@Param("id")Long id);
}
