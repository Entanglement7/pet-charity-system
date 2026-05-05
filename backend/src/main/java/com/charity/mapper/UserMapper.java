package com.charity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charity.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM users WHERE username = #{username} AND deleted = 0")
    User findByUsername(String username);
}
