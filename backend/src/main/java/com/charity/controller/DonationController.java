package com.charity.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.common.PageResult;
import com.charity.common.R;
import com.charity.entity.Donation;
import com.charity.entity.Logistics;
import com.charity.security.LoginUser;
import com.charity.service.DonationService;
import com.charity.service.LogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "捐赠管理")
@RestController
@RequestMapping("/api/donation")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final LogisticsService logisticsService;

    @Operation(summary = "发布捐赠")
    @PostMapping("/publish")
    @PreAuthorize("hasRole('USER')")
    public R<Donation> publish(@RequestBody Donation donation,
                               @AuthenticationPrincipal LoginUser loginUser) {
        donation.setUserId(loginUser.getId());
        return R.ok(donationService.publish(donation));
    }

    @Operation(summary = "捐赠列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public R<PageResult<Donation>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal LoginUser loginUser) {
        Long userId = "admin".equals(loginUser.getRole()) ? null : loginUser.getId();
        Page<Donation> result = donationService.list(userId, status, page, size);
        return R.ok(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @Operation(summary = "捐赠详情")
    @GetMapping("/detail/{id}")
    public R<Donation> detail(@PathVariable Long id) {
        return R.ok(donationService.getDetail(id));
    }

    @Operation(summary = "更新捐赠")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<Donation> update(@PathVariable Long id, @RequestBody Donation donation,
                              @AuthenticationPrincipal LoginUser loginUser) {
        return R.ok(donationService.update(id, loginUser.getId(), donation));
    }

    @Operation(summary = "删除捐赠")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<Void> delete(@PathVariable Long id,
                          @AuthenticationPrincipal LoginUser loginUser) {
        donationService.delete(id, loginUser.getId());
        return R.ok();
    }

    @Operation(summary = "获取物流信息")
    @GetMapping("/logistics/{id}")
    public R<Logistics> getLogistics(@PathVariable Long id) {
        return R.ok(logisticsService.getByRef("donation", id));
    }

    @Operation(summary = "确认收货")
    @PostMapping("/confirm/{id}")
    @PreAuthorize("hasRole('USER')")
    public R<Void> confirm(@PathVariable Long id) {
        Donation d = donationService.getDetail(id);
        d.setStatus(4);
        return R.ok();
    }
}
