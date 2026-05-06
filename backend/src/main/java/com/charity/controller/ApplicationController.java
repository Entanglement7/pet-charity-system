package com.charity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.common.PageResult;
import com.charity.common.R;
import com.charity.dto.ApplicationRecordVO;
import com.charity.entity.Application;
import com.charity.entity.Item;
import com.charity.entity.Logistics;
import com.charity.entity.Organization;
import com.charity.mapper.ItemMapper;
import com.charity.security.LoginUser;
import com.charity.service.ApplicationService;
import com.charity.service.LogisticsService;
import com.charity.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "物资申领")
@RestController
@RequestMapping("/api/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final OrganizationService organizationService;
    private final LogisticsService logisticsService;
    private final ItemMapper itemMapper;

    @Operation(summary = "获取可申领物资")
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ADMIN')")
    public R<PageResult<Item>> getAvailable(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Item> result = applicationService.getAvailableItems(category, page, size);
        return R.ok(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "申领物资")
    @PostMapping("/apply")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Application> apply(@RequestBody Application application,
                                @AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.getByUserId(loginUser.getId());
        application.setOrganizationId(org.getId());
        return R.ok(applicationService.apply(application));
    }

    @Operation(summary = "申领记录")
    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ADMIN')")
    public R<PageResult<ApplicationRecordVO>> getRecords(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.findByUserId(loginUser.getId());
        if (org == null) {
            return R.ok(new PageResult<>(0L, List.of()));
        }
        Page<Application> result = applicationService.getRecords(org.getId(), status, page, size);
        List<Application> apps = result.getRecords();
        List<Long> itemIds = apps.stream().map(Application::getItemId).distinct().collect(Collectors.toList());
        Map<Long, Item> itemMap = itemIds.isEmpty() ? Map.of() :
                itemMapper.selectBatchIds(itemIds).stream().collect(Collectors.toMap(Item::getId, i -> i));
        List<ApplicationRecordVO> vos = apps.stream().map(a -> {
            ApplicationRecordVO vo = new ApplicationRecordVO();
            vo.setId(a.getId());
            vo.setItemId(a.getItemId());
            Item item = itemMap.get(a.getItemId());
            vo.setItemTitle(item != null ? item.getTitle() : "物品#" + a.getItemId());
            vo.setItemCategory(item != null ? item.getCategory() : null);
            vo.setQuantity(a.getQuantity());
            vo.setReason(a.getReason());
            vo.setStatus(a.getStatus());
            vo.setRejectReason(a.getRejectReason());
            vo.setUsageReport(a.getUsageReport());
            vo.setUsageStatus(a.getUsageStatus());
            vo.setUsageRejectReason(a.getUsageRejectReason());
            vo.setCreatedAt(a.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
        return R.ok(new PageResult<>(result.getTotal(), vos));
    }

    @Operation(summary = "申领详情")
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Application> getDetail(@PathVariable Long id) {
        return R.ok(applicationService.getDetail(id));
    }

    @Operation(summary = "申领物流信息")
    @GetMapping("/logistics/{id}")
    @PreAuthorize("hasAnyRole('ORGANIZATION', 'ADMIN')")
    public R<Logistics> getLogistics(@PathVariable Long id) {
        return R.ok(logisticsService.getByRef("application", id));
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirm/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Void> confirmReceipt(@PathVariable Long id,
                                  @AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.getByUserId(loginUser.getId());
        applicationService.confirmReceipt(id, org.getId());
        return R.ok();
    }

    @Operation(summary = "提交物资使用情况")
    @PostMapping("/usage/{id}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    public R<Void> submitUsage(@PathVariable Long id,
                               @RequestBody Map<String, String> body,
                               @AuthenticationPrincipal LoginUser loginUser) {
        Organization org = organizationService.getByUserId(loginUser.getId());
        applicationService.submitUsageReport(id, org.getId(), body.get("report"));
        return R.ok();
    }
}
