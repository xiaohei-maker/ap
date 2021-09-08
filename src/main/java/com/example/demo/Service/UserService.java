package com.example.demo.Service;

import com.example.demo.Mapper.UserMapper;
import com.example.demo.Model.User;
import com.example.demo.Model.UserExample;
import com.example.uitils.EmailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    //GitHub 登录和修改
    public void cerateOrupdata(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0) {
            // 插入
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        } else {
            //更新
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, example);
        }
    }

    public String createUser(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().
                andNameEqualTo(user.getName());
        List<User> users = userMapper.selectByExample(userExample);
        EmailUtils.sendEmail(user);
        if(users.size() != 0){
            return  null;
        }
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(user.getGmtCreate());
        user.setStatus(0);
        String a= String.valueOf(userMapper.insert(user));
        return  a;
    }




    public User LoginUser(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().
                andNameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        for (User user1:users) {
            if(user1.getName().equals(username)){
                if (user1.getStatus()==null){
                    UserExample example = new UserExample();
                    user1.setStatus(1);
                    example.createCriteria()
                            .andIdEqualTo(user1.getId());
                    userMapper.updateByExampleSelective(user1, example);
                }
                if (user1.getStatus().equals(0)){
                    return user1;
                }else {
                    UserExample example = new UserExample();
                    user1.setGmtModified(System.currentTimeMillis());
                    example.createCriteria()
                            .andIdEqualTo(user1.getId());
                    userMapper.updateByExampleSelective(user1, example);
                    return  user1;
                }
            }
        }
        return  null;
    }

    public Integer selectCode(String c) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().
                andCodeEqualTo(c);
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size() != 0){
            for (User user1:users) {
                if(user1.getCode().equals(c)){
                    //更新
                    UserExample example = new UserExample();
                    user1.setStatus(1);
                    example.createCriteria()
                            .andIdEqualTo(user1.getId());
                    userMapper.updateByExampleSelective(user1, example);
                    return  1;
                }
            }
        }

        return 0;
    }

    public String selectUsername(String username) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().
                andNameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size() != 0){
            return  "1";
        }else {
            return "0";
        }

    }
}
