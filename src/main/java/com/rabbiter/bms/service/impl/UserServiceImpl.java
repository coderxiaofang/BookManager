package com.rabbiter.bms.service.impl;

import com.rabbiter.bms.mapper.BorrowMapper;
import com.rabbiter.bms.mapper.UserMapper;
import com.rabbiter.bms.model.Sysuser;
import com.rabbiter.bms.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Resource
    private BorrowMapper borrowMapper;

    @Override
    public Sysuser login(Sysuser sysuser) {
        return userMapper.selectByUsernameAndPasswordAndIsAdmin(sysuser.getUsername(), sysuser.getUserpassword(), sysuser.getIsadmin());
    }

    @Override
    public void saveUser(String token, Sysuser sysuser) {
        // 设置redisTemplate对象key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // key是token，value是用户保存到redis中，超时时间1小时
        redisTemplate.opsForValue().set(token, sysuser, 1, TimeUnit.HOURS);
    }

    @Override
    public Sysuser getUser(String token) {
        // 根据token得到user
        return (Sysuser) redisTemplate.opsForValue().get(token);
    }

    @Override
    public void removeUser(String token) {
        // 移除token
        redisTemplate.delete(token);
    }

    @Override
    public Integer register(String username, String password) {
        Sysuser tmp = userMapper.selectByUsername(username);
        if(tmp != null) return 0;  //账号重复

        Sysuser sysuser = new Sysuser();
        sysuser.setUsername(username);
        sysuser.setUserpassword(password);
        sysuser.setIsadmin((byte)0);
        return userMapper.insertSelective(sysuser);
    }

    @Override
    public void setPassword(Integer id, String password) {
        Sysuser sysuser = new Sysuser();
        sysuser.setUserid(id);
        sysuser.setUserpassword(password);
        userMapper.updateByPrimaryKeySelective(sysuser);
    }

    @Override
    public Integer getCount() {
        return userMapper.selectCount();
    }

    @Override
    public List<Sysuser> queryUsers() {
        return userMapper.selectAll();
    }

    @Override
    public int getSearchCount(Map<String, Object> params) {
        return userMapper.selectCountBySearch(params);
    }

    @Override
    public List<Sysuser> searchUsersByPage(Map<String, Object> params) {
        return userMapper.selectBySearch(params);
    }

    @Override
    public Integer addUser(Sysuser sysuser) {
        return userMapper.insertSelective(sysuser);
    }

    @Override
    public Integer deleteUser(Sysuser sysuser) {
        if(sysuser.getUserid() == 1) return -2;
        Map<String, Object> map = new HashMap<>();
        map.put("userid", sysuser.getUserid());
        if(borrowMapper.selectCountBySearch(map) > 0) {
            return -1;
        }
        return userMapper.deleteByPrimaryKey(sysuser.getUserid());
    }

    @Override
    public Integer deleteUsers(List<Sysuser> users) {
        int count = 0;
        for(Sysuser sysuser : users) {
            count += deleteUser(sysuser);
        }
        return count;
    }

    @Override
    public Integer updateUser(Sysuser sysuser) {
        return userMapper.updateByPrimaryKeySelective(sysuser);
    }

}
