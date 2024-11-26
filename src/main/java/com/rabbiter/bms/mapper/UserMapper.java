package com.rabbiter.bms.mapper;

import com.rabbiter.bms.model.Sysuser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer userid);

    int insert(Sysuser record);

    int insertSelective(Sysuser record);

    Sysuser selectByPrimaryKey(Integer userid);

    int updateByPrimaryKeySelective(Sysuser record);

    int updateByPrimaryKey(Sysuser record);

    Sysuser selectByUsernameAndPasswordAndIsAdmin(@Param("username") String username,
                                  @Param("password") String password,
                                  @Param("isAdmin") Byte isAdmin);

    Sysuser selectByUsername(String username);

    List<Sysuser> selectAllByLimit(@Param("begin") Integer begin, @Param("size") Integer size);

    Integer selectCount();

    List<Sysuser> selectAll();

    int selectCountBySearch(Map<String, Object> params);

    List<Sysuser> selectBySearch(Map<String, Object> params);
}
