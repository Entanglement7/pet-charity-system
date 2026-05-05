package com.charity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.entity.Application;
import com.charity.entity.Item;
import com.charity.exception.BusinessException;
import com.charity.mapper.ApplicationMapper;
import com.charity.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final ItemMapper itemMapper;

    @Transactional
    public Application apply(Application application) {
        Item item = itemMapper.selectById(application.getItemId());
        if (item == null) throw new BusinessException("物资不存在");
        if (item.getRemaining() < application.getQuantity()) {
            throw new BusinessException("库存不足");
        }
        application.setStatus(0);
        applicationMapper.insert(application);
        return application;
    }

    public Page<Item> getAvailableItems(String category, int page, int size) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<Item>()
                .eq(category != null, Item::getCategory, category)
                .gt(Item::getRemaining, 0)
                .orderByDesc(Item::getCreatedAt);
        return itemMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Page<Application> getRecords(Long organizationId, Integer status, int page, int size) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<Application>()
                .eq(organizationId != null, Application::getOrganizationId, organizationId)
                .eq(status != null, Application::getStatus, status)
                .orderByDesc(Application::getCreatedAt);
        return applicationMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Application getDetail(Long id) {
        Application a = applicationMapper.selectById(id);
        if (a == null) throw new BusinessException("申领记录不存在");
        return a;
    }

    @Transactional
    public void approve(Long id) {
        Application a = getDetail(id);
        if (a.getStatus() != 0) throw new BusinessException("状态不正确");
        Item item = itemMapper.selectById(a.getItemId());
        if (item.getRemaining() < a.getQuantity()) throw new BusinessException("库存不足");
        item.setRemaining(item.getRemaining() - a.getQuantity());
        itemMapper.updateById(item);
        a.setStatus(1);
        applicationMapper.updateById(a);
    }

    public void reject(Long id, String reason) {
        Application a = getDetail(id);
        if (a.getStatus() != 0) throw new BusinessException("状态不正确");
        a.setStatus(2);
        a.setRejectReason(reason);
        applicationMapper.updateById(a);
    }

    public void confirmReceipt(Long id, Long organizationId) {
        Application a = getDetail(id);
        if (!a.getOrganizationId().equals(organizationId)) throw new BusinessException("无权操作");
        if (a.getStatus() != 3) throw new BusinessException("请等待发货");
        a.setStatus(4);
        applicationMapper.updateById(a);
    }
}
