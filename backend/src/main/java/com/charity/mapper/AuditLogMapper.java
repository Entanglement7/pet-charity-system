package com.charity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charity.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
