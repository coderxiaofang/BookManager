package com.rabbiter.bms.service;

import com.rabbiter.bms.model.Sysuser;

import java.util.List;
import java.util.Map;

public interface UserService {

    Sysuser login(Sysuser sysuser);

    void saveUser(String token, Sysuser sysuser);

    Sysuser getUser(String token);

    void removeUser(String token);

    Integer register(String username, String password);

    void setPassword(Integer id, String password);

    Integer getCount();

    List<Sysuser> queryUsers();

    int getSearchCount(Map<String, Object> searchParam);

    List<Sysuser> searchUsersByPage(Map<String, Object> params);

    Integer addUser(Sysuser sysuser);

    Integer deleteUser(Sysuser sysuser);

    Integer deleteUsers(List<Sysuser> users);

    Integer updateUser(Sysuser sysuser);
}
