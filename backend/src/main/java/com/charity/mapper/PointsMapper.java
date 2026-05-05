package com.charity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charity.entity.Points;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PointsMapper extends BaseMapper<Points> {

    @Select("SELECT * FROM points WHERE user_id = #{userId}")
    Points findByUserId(Long userId);
}
