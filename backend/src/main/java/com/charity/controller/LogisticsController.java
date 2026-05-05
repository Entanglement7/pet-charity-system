package com.charity.controller;

import com.charity.common.R;
import com.charity.entity.Logistics;
import com.charity.service.LogisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "物流管理")
@RestController
@RequestMapping("/api/logistics")
@RequiredArgsConstructor
public class LogisticsController {

    private final LogisticsService logisticsService;

    @Operation(summary = "查询物流信息")
    @GetMapping("/info")
    public R<Logistics> getInfo(@RequestParam String type, @RequestParam Long id) {
        return R.ok(logisticsService.getByRef(type, id));
    }

    @Operation(summary = "更新物流信息")
    @PostMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Logistics> update(@RequestBody Logistics logistics) {
        return R.ok(logisticsService.update(logistics));
    }

    @Operation(summary = "获取物流公司列表")
    @GetMapping("/companies")
    public R<List<String>> getCompanies() {
        return R.ok(logisticsService.getCompanies());
    }
}
