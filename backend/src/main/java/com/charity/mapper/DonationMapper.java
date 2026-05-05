package com.charity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charity.entity.Donation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DonationMapper extends BaseMapper<Donation> {
}
