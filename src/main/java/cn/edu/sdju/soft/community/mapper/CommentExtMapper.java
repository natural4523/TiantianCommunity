package cn.edu.sdju.soft.community.mapper;

import cn.edu.sdju.soft.community.model.Comment;
import org.apache.ibatis.annotations.Param;

public interface CommentExtMapper {

    int incCommentCount(Comment comment);

    void deleteComment(@Param("id") Long id);

    Comment findByCommentId(@Param("id") Long id);
}