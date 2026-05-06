package com.charity.service;

import com.charity.entity.Application;
import com.charity.entity.Donation;
import com.charity.entity.Logistics;
import com.charity.exception.BusinessException;
import com.charity.mapper.ApplicationMapper;
import com.charity.mapper.DonationMapper;
import com.charity.mapper.LogisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogisticsService {

    private final LogisticsMapper logisticsMapper;
    private final ApplicationMapper applicationMapper;
    private final DonationMapper donationMapper;

    public Logistics getByRef(String refType, Long refId) {
        return logisticsMapper.findByRef(refType, refId);
    }

    @Transactional
    public Logistics update(Logistics logistics) {
        Logistics existing = logisticsMapper.findByRef(logistics.getRefType(), logistics.getRefId());
        if (existing != null) {
            logistics.setId(existing.getId());
            if (logistics.getStatus() != null && logistics.getStatus() == 1) {
                logistics.setShippedAt(LocalDateTime.now());
            }
            logisticsMapper.updateById(logistics);
            updateRefStatus(logistics.getRefType(), logistics.getRefId(), logistics.getStatus());
            return logisticsMapper.selectById(existing.getId());
        } else {
            if (logistics.getStatus() != null && logistics.getStatus() == 1) {
                logistics.setShippedAt(LocalDateTime.now());
            }
            logisticsMapper.insert(logistics);
            updateRefStatus(logistics.getRefType(), logistics.getRefId(), logistics.getStatus());
            return logistics;
        }
    }

    private void updateRefStatus(String refType, Long refId, Integer logisticsStatus) {
        if (logisticsStatus == null) return;

        if ("application".equals(refType)) {
            Application app = applicationMapper.selectById(refId);
            if (app != null && logisticsStatus == 1 && app.getStatus() == 1) {
                // 发货时，将申领状态从"审核通过"更新为"已发货"
                app.setStatus(3);
                applicationMapper.updateById(app);
            }
        } else if ("donation".equals(refType)) {
            Donation donation = donationMapper.selectById(refId);
            if (donation != null && logisticsStatus == 1 && donation.getStatus() == 1) {
                // 发货时，将捐赠状态从"审核通过"更新为"已发货"
                donation.setStatus(3);
                donationMapper.updateById(donation);
            }
        }
    }

    public java.util.List<String> getCompanies() {
        return java.util.List.of("顺丰速运", "中通快递", "圆通速递", "韵达快递", "申通快递", "极兔速递");
    }
}
