package com.charity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.common.PageResult;
import com.charity.common.R;
import com.charity.dto.ApplicationAuditVO;
import com.charity.entity.AuditLog;
import com.charity.entity.Application;
import com.charity.entity.Donation;
import com.charity.entity.Item;
import com.charity.entity.Organization;
import com.charity.mapper.ItemMapper;
import com.charity.mapper.OrganizationMapper;
import com.charity.security.LoginUser;
import com.charity.service.AuditService;
import com.charity.service.ApplicationService;
import com.charity.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "审核管理")
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;
    private final DonationService donationService;
    private final ApplicationService applicationService;
    private final OrganizationMapper organizationMapper;
    private final ItemMapper itemMapper;

    @Operation(summary = "机构审核列表")
    @GetMapping("/organization/list")
    public R<PageResult<Organization>> getOrganizationList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Organization> result = auditService.getOrganizationAuditList(status, page, size);
        return R.ok(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "审核机构")
    @PostMapping("/organization/{id}")
    public R<Void> auditOrganization(@PathVariable Long id,
                                     @RequestBody Map<String, String> body,
                                     @AuthenticationPrincipal LoginUser loginUser) {
        auditService.auditOrganization(id, loginUser.getId(), body.get("action"), body.get("reason"));
        return R.ok();
    }

    @Operation(summary = "捐赠审核列表")
    @GetMapping("/donation/list")
    public R<PageResult<Donation>> getDonationList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Donation> result = donationService.list(null, status, page, size);
        return R.ok(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "审核捐赠")
    @PostMapping("/donation/{id}")
    public R<Void> auditDonation(@PathVariable Long id,
                                 @RequestBody Map<String, String> body,
                                 @AuthenticationPrincipal LoginUser loginUser) {
        auditService.auditDonation(id, loginUser.getId(), body.get("action"), body.get("reason"));
        return R.ok();
    }

    @Operation(summary = "申领审核列表")
    @GetMapping("/application/list")
    public R<PageResult<ApplicationAuditVO>> getApplicationList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Application> result = applicationService.getRecords(null, status, page, size);
        List<Application> records = result.getRecords();

        // 批量查机构名和物品名
        Map<Long, String> orgNames = records.stream()
                .map(Application::getOrganizationId).distinct()
                .collect(java.util.stream.Collectors.toMap(
                        id -> id,
                        id -> { Organization o = organizationMapper.selectById(id); return o != null ? o.getName() : "—"; }
                ));
        Map<Long, String> itemTitles = records.stream()
                .map(Application::getItemId).distinct()
                .collect(java.util.stream.Collectors.toMap(
                        id -> id,
                        id -> { Item i = itemMapper.selectById(id); return i != null ? i.getTitle() : "—"; }
                ));

        List<ApplicationAuditVO> vos = records.stream().map(a -> {
            ApplicationAuditVO vo = new ApplicationAuditVO();
            vo.setId(a.getId());
            vo.setOrganizationId(a.getOrganizationId());
            vo.setOrganizationName(orgNames.getOrDefault(a.getOrganizationId(), "—"));
            vo.setItemId(a.getItemId());
            vo.setItemTitle(itemTitles.getOrDefault(a.getItemId(), "—"));
            vo.setQuantity(a.getQuantity());
            vo.setReason(a.getReason());
            vo.setStatus(a.getStatus());
            vo.setCreatedAt(a.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());

        return R.ok(new PageResult<>(result.getTotal(), vos));
    }

    @Operation(summary = "审核申领")
    @PostMapping("/application/{id}")
    public R<Void> auditApplication(@PathVariable Long id,
                                    @RequestBody Map<String, String> body,
                                    @AuthenticationPrincipal LoginUser loginUser) {
        auditService.auditApplication(id, loginUser.getId(), body.get("action"), body.get("reason"));
        return R.ok();
    }

    @Operation(summary = "审核历史")
    @GetMapping("/history")
    public R<PageResult<AuditLog>> getHistory(
            @RequestParam(required = false) String refType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AuditLog> result = auditService.getHistory(refType, page, size);
        return R.ok(new PageResult<>(result.getTotal(), result.getRecords()));
    }
}
