package cn.edu.sdju.soft.community.mapper;

import cn.edu.sdju.soft.community.dto.QuestionQueryDTO;
import cn.edu.sdju.soft.community.model.Question;
import cn.edu.sdju.soft.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {
    int incView(Question record);
    int incCommentCount(Question record);
    List<Question> selectRelated(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);
}