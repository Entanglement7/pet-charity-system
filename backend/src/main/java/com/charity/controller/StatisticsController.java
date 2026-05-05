package com.charity.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charity.common.R;
import com.charity.entity.*;
import com.charity.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "数据统计")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final UserMapper userMapper;
    private final DonationMapper donationMapper;
    private final ApplicationMapper applicationMapper;
    private final OrganizationMapper organizationMapper;

    @Operation(summary = "统计概览")
    @GetMapping("/index")
    public R<Map<String, Object>> getOverview() {
        long totalUsers = userMapper.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getRole, "user"));
        long totalOrgs = organizationMapper.selectCount(
            new LambdaQueryWrapper<Organization>().eq(Organization::getStatus, 1));
        long pendingOrganizations = organizationMapper.selectCount(
            new LambdaQueryWrapper<Organization>().eq(Organization::getStatus, 0));
        long totalDonations = donationMapper.selectCount(
            new LambdaQueryWrapper<Donation>().in(Donation::getStatus, 1, 3, 4));
        long pendingDonations = donationMapper.selectCount(
            new LambdaQueryWrapper<Donation>().eq(Donation::getStatus, 0));
        long totalApplications = applicationMapper.selectCount(null);
        long pendingApplications = applicationMapper.selectCount(
            new LambdaQueryWrapper<Application>().eq(Application::getStatus, 0));

        return R.ok(Map.of(
            "totalUsers", totalUsers,
            "totalOrgs", totalOrgs,
            "pendingOrganizations", pendingOrganizations,
            "totalDonations", totalDonations,
            "pendingDonations", pendingDonations,
            "totalApplications", totalApplications,
            "pendingApplications", pendingApplications
        ));
    }
}
