package com.charity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charity.entity.Logistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LogisticsMapper extends BaseMapper<Logistics> {

    @Select("SELECT * FROM logistics WHERE ref_type = #{refType} AND ref_id = #{refId}")
    Logistics findByRef(String refType, Long refId);
}
