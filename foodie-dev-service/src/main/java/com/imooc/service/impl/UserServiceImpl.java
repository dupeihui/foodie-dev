package com.imooc.service.impl;

import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserServiceImpl implements UsersService {

    @Autowired
    private UsersMapper usersMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users getUserInfo(String id) {
        return usersMapper.selectByPrimaryKey(id);
    }

    @Override
    public void saveUser() {
        Users users = new Users();
        users.setId("2");
        users.setUsername("Senyi");
        users.setPassword("123");
        users.setFace("123");
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.insert(users);
    }

    @Override
    public void updateUser(String id) {
        Users users = new Users();
        users.setId(id);
        users.setPassword("123");
        users.setUsername("cxr");
        users.setFace("123");
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.updateByPrimaryKey(users);
    }

    @Override
    public void deleteUser(String id) {
        usersMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void saveParent() {
        Users users = new Users();
        users.setId("3");
        users.setUsername("parent");
        users.setPassword("123");
        users.setFace("123");
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.insert(users);
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public void saveChildren() {
        saveChild1();
        int a = 1/0;
        saveChild2();
    }

    public void saveChild1() {
        Users users = new Users();
        users.setId("4");
        users.setUsername("child-1");
        users.setPassword("123456");
        users.setFace("123456");
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.insert(users);
    }

    public void saveChild2() {
        Users users = new Users();
        users.setId("5");
        users.setUsername("child-2");
        users.setPassword("123");
        users.setFace("123");
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());

        usersMapper.insert(users);
    }
}
