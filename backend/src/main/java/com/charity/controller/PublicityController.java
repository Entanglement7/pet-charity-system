package com.charity.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.common.PageResult;
import com.charity.common.R;
import com.charity.dto.FlowVO;
import com.charity.dto.PublicityRecordVO;
import com.charity.entity.*;
import com.charity.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "公益公示")
@RestController
@RequestMapping("/api/publicity")
@RequiredArgsConstructor
public class PublicityController {

    private final DonationMapper donationMapper;
    private final LogisticsMapper logisticsMapper;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final OrganizationMapper organizationMapper;

    @Operation(summary = "捐赠记录公示")
    @GetMapping("/records")
    public R<PageResult<PublicityRecordVO>> getRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Donation> result = donationMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<Donation>()
                .in(Donation::getStatus, 1, 3, 4)
                .orderByDesc(Donation::getCreatedAt)
        );
        List<Donation> donations = result.getRecords();
        List<Long> userIds = donations.stream().map(Donation::getUserId).distinct().collect(Collectors.toList());
        Map<Long, String> usernameMap = userIds.isEmpty() ? Map.of() :
            userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getUsername));
        List<PublicityRecordVO> vos = donations.stream().map(d -> {
            PublicityRecordVO vo = new PublicityRecordVO();
            vo.setId(d.getId());
            vo.setDonorName(usernameMap.getOrDefault(d.getUserId(), "用户#" + d.getUserId()));
            vo.setTitle(d.getTitle());
            vo.setCategory(d.getCategory());
            vo.setQuantity(d.getQuantity());
            vo.setUnit(d.getUnit());
            vo.setStatus(d.getStatus());
            vo.setPointsAwarded(d.getPointsAwarded());
            vo.setCreatedAt(d.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
        return R.ok(new PageResult<>(result.getTotal(), vos));
    }

    @Operation(summary = "物资流向")
    @GetMapping("/flow")
    public R<PageResult<FlowVO>> getFlow(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Logistics> result = logisticsMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<Logistics>().orderByDesc(Logistics::getCreatedAt)
        );
        List<Logistics> logistics = result.getRecords();

        // 批量获取 donation 关联的物品名和捐赠人
        List<Long> donationRefIds = logistics.stream()
            .filter(l -> "donation".equals(l.getRefType()))
            .map(Logistics::getRefId).distinct().collect(Collectors.toList());
        Map<Long, Donation> donationMap = donationRefIds.isEmpty() ? Map.of() :
            donationMapper.selectBatchIds(donationRefIds).stream()
                .collect(Collectors.toMap(Donation::getId, d -> d));
        List<Long> donorUserIds = donationMap.values().stream()
            .map(Donation::getUserId).distinct().collect(Collectors.toList());
        Map<Long, String> usernameMap = donorUserIds.isEmpty() ? Map.of() :
            userMapper.selectBatchIds(donorUserIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        List<FlowVO> vos = logistics.stream().map(l -> {
            FlowVO vo = new FlowVO();
            vo.setId(l.getId());
            vo.setRefType(l.getRefType());
            vo.setRefId(l.getRefId());
            vo.setCompany(l.getCompany());
            vo.setTrackingNo(l.getTrackingNo());
            vo.setStatus(l.getStatus());
            vo.setReceiverName(l.getReceiverName());
            vo.setReceiverAddress(l.getReceiverAddress());
            vo.setShippedAt(l.getShippedAt());
            vo.setReceivedAt(l.getReceivedAt());
            vo.setCreatedAt(l.getCreatedAt());
            if ("donation".equals(l.getRefType())) {
                Donation d = donationMap.get(l.getRefId());
                if (d != null) {
                    vo.setItemName(d.getTitle());
                    vo.setDonorName(usernameMap.getOrDefault(d.getUserId(), "用户#" + d.getUserId()));
                }
            }
            return vo;
        }).collect(Collectors.toList());
        return R.ok(new PageResult<>(result.getTotal(), vos));
    }
}
