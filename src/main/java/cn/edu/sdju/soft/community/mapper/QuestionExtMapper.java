package cn.edu.sdju.soft.community.mapper;

import cn.edu.sdju.soft.community.model.Question;
import cn.edu.sdju.soft.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);
    int incCommentCount(Question record);
}