package com.charity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charity.entity.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    @Select("SELECT * FROM organizations WHERE user_id = #{userId} AND deleted = 0")
    Organization findByUserId(Long userId);
}
