package com.charity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.entity.AuditLog;
import com.charity.entity.Organization;
import com.charity.mapper.AuditLogMapper;
import com.charity.mapper.OrganizationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogMapper auditLogMapper;
    private final OrganizationMapper organizationMapper;
    private final DonationService donationService;
    private final ApplicationService applicationService;

    public Page<Organization> getOrganizationAuditList(Integer status, int page, int size) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<Organization>()
                .eq(status != null, Organization::getStatus, status)
                .orderByDesc(Organization::getCreatedAt);
        return organizationMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Transactional
    public void auditOrganization(Long id, Long auditorId, String action, String reason) {
        Organization org = organizationMapper.selectById(id);
        if (org == null) throw new com.charity.exception.BusinessException("机构不存在");
        org.setStatus("approve".equals(action) ? 1 : 2);
        if ("reject".equals(action)) org.setRejectReason(reason);
        organizationMapper.updateById(org);
        saveLog(auditorId, "organization", id, action, reason);
    }

    @Transactional
    public void auditDonation(Long id, Long auditorId, String action, String reason) {
        if ("approve".equals(action)) {
            donationService.approve(id, auditorId);
        } else {
            donationService.reject(id, reason);
        }
        saveLog(auditorId, "donation", id, action, reason);
    }

    @Transactional
    public void auditApplication(Long id, Long auditorId, String action, String reason) {
        if ("approve".equals(action)) {
            applicationService.approve(id);
        } else {
            applicationService.reject(id, reason);
        }
        saveLog(auditorId, "application", id, action, reason);
    }

    public Page<AuditLog> getHistory(String refType, int page, int size) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<AuditLog>()
                .eq(refType != null, AuditLog::getRefType, refType)
                .orderByDesc(AuditLog::getCreatedAt);
        return auditLogMapper.selectPage(new Page<>(page, size), wrapper);
    }

    private void saveLog(Long auditorId, String refType, Long refId, String action, String reason) {
        AuditLog log = new AuditLog();
        log.setAuditorId(auditorId);
        log.setRefType(refType);
        log.setRefId(refId);
        log.setAction(action);
        log.setReason(reason);
        auditLogMapper.insert(log);
    }
}
