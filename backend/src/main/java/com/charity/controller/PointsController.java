package com.charity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.common.PageResult;
import com.charity.common.R;
import com.charity.dto.ExchangeRecordVO;
import com.charity.dto.UserPointsVO;
import com.charity.entity.ExchangeItem;
import com.charity.entity.ExchangeRecord;
import com.charity.entity.Logistics;
import com.charity.entity.Points;
import com.charity.entity.PointsRecord;
import com.charity.entity.User;
import com.charity.mapper.ExchangeItemMapper;
import com.charity.mapper.UserMapper;
import com.charity.security.LoginUser;
import com.charity.service.ExchangeService;
import com.charity.service.LogisticsService;
import com.charity.service.PointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "积分管理")
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointsController {

    private final PointsService pointsService;
    private final ExchangeService exchangeService;
    private final LogisticsService logisticsService;
    private final UserMapper userMapper;
    private final ExchangeItemMapper exchangeItemMapper;

    @Operation(summary = "积分信息")
    @GetMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public R<Points> getInfo(@AuthenticationPrincipal LoginUser loginUser) {
        return R.ok(pointsService.getOrCreate(loginUser.getId()));
    }

    @Operation(summary = "积分明细")
    @GetMapping("/detail")
    @PreAuthorize("hasRole('USER')")
    public R<PageResult<PointsRecord>> getDetail(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal LoginUser loginUser) {
        Page<PointsRecord> result = pointsService.getRecords(loginUser.getId(), page, size);
        return R.ok(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "可兑换物品列表")
    @GetMapping("/exchange/list")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public R<PageResult<ExchangeItem>> getExchangeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ExchangeItem> result = exchangeService.getExchangeList(page, size);
        return R.ok(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "新增兑换物品")
    @PostMapping("/exchange/item")
    @PreAuthorize("hasRole('ADMIN')")
    public R<ExchangeItem> addExchangeItem(@RequestBody ExchangeItem item) {
        item.setId(null);
        exchangeItemMapper.insert(item);
        return R.ok(item);
    }

    @Operation(summary = "修改兑换物品")
    @PutMapping("/exchange/item/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<ExchangeItem> updateExchangeItem(@PathVariable Long id, @RequestBody ExchangeItem item) {
        item.setId(id);
        exchangeItemMapper.updateById(item);
        return R.ok(exchangeItemMapper.selectById(id));
    }

    @Operation(summary = "删除兑换物品")
    @DeleteMapping("/exchange/item/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> deleteExchangeItem(@PathVariable Long id) {
        exchangeItemMapper.deleteById(id);
        return R.ok();
    }

    @Operation(summary = "兑换物品")
    @PostMapping("/exchange")
    @PreAuthorize("hasRole('USER')")
    public R<ExchangeRecord> exchange(@RequestBody Map<String, Object> body,
                                      @AuthenticationPrincipal LoginUser loginUser) {
        Long itemId = Long.valueOf(body.get("itemId").toString());
        Integer quantity = body.containsKey("quantity") ?
                Integer.valueOf(body.get("quantity").toString()) : 1;
        String deliveryAddress = body.containsKey("deliveryAddress") ?
                body.get("deliveryAddress").toString() : "";
        return R.ok(exchangeService.exchange(loginUser.getId(), itemId, quantity, deliveryAddress));
    }

    @Operation(summary = "兑换记录")
    @GetMapping("/exchange/records")
    @PreAuthorize("hasRole('USER')")
    public R<PageResult<ExchangeRecordVO>> getExchangeRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal LoginUser loginUser) {
        Page<ExchangeRecord> result = exchangeService.getExchangeRecords(loginUser.getId(), page, size);
        List<ExchangeRecord> records = result.getRecords();
        List<Long> itemIds = records.stream().map(ExchangeRecord::getItemId).distinct().collect(Collectors.toList());
        Map<Long, String> itemNameMap = itemIds.isEmpty() ? Map.of() :
            exchangeItemMapper.selectBatchIds(itemIds).stream()
                .collect(Collectors.toMap(com.charity.entity.ExchangeItem::getId, com.charity.entity.ExchangeItem::getName));
        List<ExchangeRecordVO> vos = records.stream().map(r -> {
            ExchangeRecordVO vo = new ExchangeRecordVO();
            vo.setId(r.getId());
            vo.setItemId(r.getItemId());
            vo.setItemName(itemNameMap.getOrDefault(r.getItemId(), "物品#" + r.getItemId()));
            vo.setQuantity(r.getQuantity());
            vo.setPointsCost(r.getPointsCost());
            vo.setDeliveryAddress(r.getDeliveryAddress());
            vo.setStatus(r.getStatus());
            vo.setCreatedAt(r.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
        return R.ok(new PageResult<>(result.getTotal(), vos));
    }

    @Operation(summary = "兑换物流信息")
    @GetMapping("/exchange/logistics/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<Logistics> getExchangeLogistics(@PathVariable Long id) {
        return R.ok(logisticsService.getByRef("exchange", id));
    }

    @Operation(summary = "确认收货")
    @PostMapping("/exchange/confirm/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<Void> confirmReceipt(@PathVariable Long id,
                                  @AuthenticationPrincipal LoginUser loginUser) {
        exchangeService.confirmReceipt(id, loginUser.getId());
        return R.ok();
    }

    @Operation(summary = "用户积分列表")
    @GetMapping("/user/list")
    @PreAuthorize("hasRole('ADMIN')")
    public R<PageResult<UserPointsVO>> getUserPointsList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Points> result = pointsService.getUserPointsList(page, size);
        List<Points> records = result.getRecords();
        List<Long> userIds = records.stream().map(Points::getUserId).distinct().collect(Collectors.toList());
        Map<Long, String> usernameMap = userIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, User::getUsername));
        List<UserPointsVO> vos = records.stream().map(p -> {
            UserPointsVO vo = new UserPointsVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(usernameMap.getOrDefault(p.getUserId(), "用户#" + p.getUserId()));
            vo.setBalance(p.getBalance());
            vo.setTotalEarned(p.getTotalEarned());
            vo.setTotalSpent(p.getTotalSpent());
            vo.setUpdatedAt(p.getUpdatedAt());
            return vo;
        }).collect(Collectors.toList());
        return R.ok(new PageResult<>(result.getTotal(), vos));
    }
}
