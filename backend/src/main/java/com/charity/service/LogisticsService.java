package com.charity.service;

import com.charity.entity.AuditLog;
import com.charity.entity.Logistics;
import com.charity.exception.BusinessException;
import com.charity.mapper.AuditLogMapper;
import com.charity.mapper.LogisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogisticsService {

    private final LogisticsMapper logisticsMapper;

    public Logistics getByRef(String refType, Long refId) {
        return logisticsMapper.findByRef(refType, refId);
    }

    public Logistics update(Logistics logistics) {
        Logistics existing = logisticsMapper.findByRef(logistics.getRefType(), logistics.getRefId());
        if (existing != null) {
            logistics.setId(existing.getId());
            if (logistics.getStatus() != null && logistics.getStatus() == 1) {
                logistics.setShippedAt(LocalDateTime.now());
            }
            logisticsMapper.updateById(logistics);
            return logisticsMapper.selectById(existing.getId());
        } else {
            logisticsMapper.insert(logistics);
            return logistics;
        }
    }

    public List<String> getCompanies() {
        return List.of("顺丰速运", "中通快递", "圆通速递", "韵达快递", "申通快递", "极兔速递");
    }
}
