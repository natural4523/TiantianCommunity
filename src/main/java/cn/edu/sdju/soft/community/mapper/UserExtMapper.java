package cn.edu.sdju.soft.community.mapper;

import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.model.UserExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface UserExtMapper {

    User findByUsername(@Param("username") String username, @Param("password") String password);

    void createUser(User user);
}