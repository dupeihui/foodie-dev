package com.imooc.service;

import com.imooc.pojo.Users;

public interface UsersService {

    public Users getUserInfo(String id);

    public void saveUser();

    public void updateUser(String id);

    public void deleteUser(String id);

    public void saveParent();

    public void saveChildren();

}
