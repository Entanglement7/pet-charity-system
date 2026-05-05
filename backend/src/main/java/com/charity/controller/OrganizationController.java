package com.charity.controller;

import com.charity.common.R;
import com.charity.entity.Organization;
import com.charity.entity.OrganizationAddress;
import com.charity.mapper.OrganizationMapper;
import com.charity.security.LoginUser;
import com.charity.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "机构管理")
@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final OrganizationMapper organizationMapper;

    @Operation(summary = "注册机构")
    @PostMapping("/register")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Organization> register(@RequestBody Organization org,
                                    @AuthenticationPrincipal LoginUser loginUser) {
        org.setUserId(loginUser.getId());
        return R.ok(organizationService.register(org));
    }

    @Operation(summary = "获取机构信息")
    @GetMapping("/info")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Organization> getInfo(@AuthenticationPrincipal LoginUser loginUser) {
        return R.ok(organizationService.getByUserId(loginUser.getId()));
    }

    @Operation(summary = "更新机构信息")
    @PutMapping("/update")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Organization> update(@RequestBody Organization org,
                                  @AuthenticationPrincipal LoginUser loginUser) {
        return R.ok(organizationService.update(loginUser.getId(), org));
    }

    @Operation(summary = "获取认证状态")
    @GetMapping("/verification/status")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Map<String, Object>> getVerificationStatus(@AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.getByUserId(loginUser.getId());
        return R.ok(Map.of("status", org.getStatus(),
                           "rejectReason", org.getRejectReason() != null ? org.getRejectReason() : ""));
    }

    @Operation(summary = "获取地址列表")
    @GetMapping("/address/list")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<List<OrganizationAddress>> getAddressList(@AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.getByUserId(loginUser.getId());
        return R.ok(organizationService.getAddressList(org.getId()));
    }

    @Operation(summary = "添加地址")
    @PostMapping("/address/add")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<OrganizationAddress> addAddress(@RequestBody OrganizationAddress address,
                                             @AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.getByUserId(loginUser.getId());
        address.setOrganizationId(org.getId());
        return R.ok(organizationService.addAddress(address));
    }

    @Operation(summary = "更新地址")
    @PutMapping("/address/update/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<OrganizationAddress> updateAddress(@PathVariable Long id,
                                                @RequestBody OrganizationAddress address,
                                                @AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.getByUserId(loginUser.getId());
        return R.ok(organizationService.updateAddress(id, org.getId(), address));
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/address/delete/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Void> deleteAddress(@PathVariable Long id,
                                 @AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.getByUserId(loginUser.getId());
        organizationService.deleteAddress(id, org.getId());
        return R.ok();
    }
}
