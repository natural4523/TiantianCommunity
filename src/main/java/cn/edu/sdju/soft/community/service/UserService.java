package cn.edu.sdju.soft.community.service;

import cn.edu.sdju.soft.community.dto.PaginationDTO;
import cn.edu.sdju.soft.community.mapper.UserExtMapper;
import cn.edu.sdju.soft.community.mapper.UserMapper;
import cn.edu.sdju.soft.community.model.CheckQuestions;
import cn.edu.sdju.soft.community.model.User;
import cn.edu.sdju.soft.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserExtMapper userExtMapper;

    public void createOrUpdate(User user) {
        //User dbUser = userMapper.findByAccountId(user.getAccountId());
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0){
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            //更新
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtCreate(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria().andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser,example);
            //userMapper.update(dbUser);
        }
    }

    public User findByUsername(String username, String password) {
        User user = userExtMapper.findByUsername(username,password);
        return user;
    }

    public void createUser(User user) {
        userExtMapper.createUser(user);
    }

    public void freezeByUserId(long id) {
        userExtMapper.freezeByUserId(id);
    }

    public User findByUserId(long id) {
        User user = userExtMapper.findByUserId(id);
        return user;
    }

    public void unfreezeByUserId(long id) {
        userExtMapper.unfreezeByUserId(id);
    }

    public void editUser(User user) {
        userExtMapper.editUser(user);
    }

    public PaginationDTO findFrozenUsers(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalPage;
        Integer totalCount = userExtMapper.countFrozenUsers();

        if (totalCount % size == 0){
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1){
            page = 1;
        }

        if (page > totalPage){
            page = totalPage;
        }

        if(page == 0){
            page = 1;
        }
        paginationDTO.setPagination(totalPage,page);

        Integer offset = size * (page - 1);

        List<User> userList = userExtMapper.findFrozenUsers(size,offset);
        paginationDTO.setData(userList);
        return paginationDTO;
    }

    public void editPassword(User user) {
        userExtMapper.editPassword(user);
    }

    public User findByUsername1(String username) {
        User user  = userExtMapper.findByUsername1(username);
        return user;
    }


}
