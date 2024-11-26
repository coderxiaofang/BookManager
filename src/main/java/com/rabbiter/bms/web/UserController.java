package com.rabbiter.bms.web;

import com.rabbiter.bms.model.Sysuser;
import com.rabbiter.bms.service.UserService;
import com.rabbiter.bms.utils.MyResult;
import com.rabbiter.bms.utils.MyUtils;
import com.rabbiter.bms.utils.TokenProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;

    // 登录
    @RequestMapping(value = "/login")
    public Map<String, Object> login(@RequestBody Sysuser sysuser) {
        // 登录
        Sysuser userObj = userService.login(sysuser);
        if(userObj == null) {   // 账号或密码错误
            // 返回结果对象
            return MyResult.getResultMap(420, "账号或密码错误");
        } else {    // 账号密码正确
            // 创建token
            String token = TokenProcessor.getInstance().makeToken();
            // 保存到Redis
            userService.saveUser(token, userObj);
            // 返回结果对象
            return MyResult.getResultMap(200, "登录成功",
                    new HashMap<String, String>(){{ put("token", token); }});
        }
    }

    // 查看用户信息
    @RequestMapping(value = "/info")
    public Map<String, Object> info(String token) {
        // 从redis中取用户
        Sysuser sysuser = userService.getUser(token);
        if(sysuser == null) {  // 获取失败
            return MyResult.getResultMap(420, "获取用户信息失败");
        } else {    // 获取成功
            return MyResult.getResultMap(200, "获取用户信息成功", sysuser);
        }
    }

    // 退出登录
    @RequestMapping(value = "/logout")
    public Map<String, Object> logout(String token) {
        // 从redis中移除用户
        userService.removeUser(token);
        return MyResult.getResultMap(200, "退出登录成功" );
    }

    // 注册
    @RequestMapping(value = "/register")
    public Integer register(String username, String password){
        return userService.register(username, password);
    }

    // 修改密码
    @RequestMapping(value = {"/alterPassword", "reader/alterPassword"})
    public Integer alterPassword(Integer userid, String username, Byte isadmin, String oldPassword, String newPassword){
        //检查旧密码是否正确
        Sysuser userObj = new Sysuser();
        userObj.setUserid(userid);
        userObj.setUsername(username);
        userObj.setUserpassword(oldPassword);
        userObj.setIsadmin(isadmin);

        Sysuser sysuser = userService.login(userObj);
        if(sysuser == null) {  //旧密码不正确
            return 0;
        } else {    //旧密码正确，设置新密码
            userService.setPassword(userObj.getUserid(), newPassword);
            return 1;
        }
    }

    // 获得数量
    @GetMapping(value = "/getCount")
    public Integer getCount(){
        return userService.getCount();
    }

    // 查询所有用户
    @GetMapping(value = "/queryUsers")
    public List<Sysuser> queryUsers(){
        return userService.queryUsers();
    }

    // 分页查询用户 params: {page, limit, username}
    @GetMapping(value = "/queryUsersByPage")
    public Map<String, Object> queryUsersByPage(@RequestParam Map<String, Object> params){
        MyUtils.parsePageParams(params);
        int count = userService.getSearchCount(params);
        List<Sysuser> users = userService.searchUsersByPage(params);
        return MyResult.getListResultMap(0, "success", count, users);
    }

    // 添加用户
    @PostMapping(value = "/addUser")
    public Integer addUser(@RequestBody Sysuser sysuser){
        return userService.addUser(sysuser);
    }

    // 删除用户
    @DeleteMapping(value = "/deleteUser")
    public Integer deleteUser(@RequestBody Sysuser sysuser){
        return userService.deleteUser(sysuser);
    }

    // 删除一些用户
    @DeleteMapping(value = "/deleteUsers")
    public Integer deleteUsers(@RequestBody List<Sysuser> users){
        return userService.deleteUsers(users);
    }

    // 更新用户
    @RequestMapping(value = "/updateUser")
    public Integer updateUser(@RequestBody Sysuser sysuser){
        return userService.updateUser(sysuser);
    }
}
