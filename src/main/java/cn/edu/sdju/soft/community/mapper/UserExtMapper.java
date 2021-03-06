package cn.edu.sdju.soft.community.mapper;

import cn.edu.sdju.soft.community.model.CheckQuestions;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.model.UserExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface UserExtMapper {

    User findByUsername(@Param("username") String username, @Param("password") String password);

    void createUser(User user);

    List<User> findListByUsername(@Param("username") String username);

    void freezeByUserId(@Param("id") long id);

    User findByUserId(@Param("id") long id);

    void unfreezeByUserId(@Param("id") long id);

    void editUser(User user);

    Integer countFrozenUsers();

    List<User> findFrozenUsers(@Param("size") Integer size, @Param("offset") Integer offset);

    void editPassword(User user);

    User findByUsername1(@Param("username") String username);

    String findAvatarUrlById(@Param("id")long id);

    User checkName(@Param("name") String name);
}