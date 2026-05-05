package com.charity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.entity.ExchangeItem;
import com.charity.entity.ExchangeRecord;
import com.charity.exception.BusinessException;
import com.charity.mapper.ExchangeItemMapper;
import com.charity.mapper.ExchangeRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeItemMapper exchangeItemMapper;
    private final ExchangeRecordMapper exchangeRecordMapper;
    private final PointsService pointsService;

    public Page<ExchangeItem> getExchangeList(int page, int size) {
        return exchangeItemMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<ExchangeItem>().gt(ExchangeItem::getStock, 0)
        );
    }

    @Transactional
    public ExchangeRecord exchange(Long userId, Long itemId, Integer quantity, String deliveryAddress) {
        ExchangeItem item = exchangeItemMapper.selectById(itemId);
        if (item == null) throw new BusinessException("兑换物品不存在");
        if (item.getStock() < quantity) throw new BusinessException("库存不足");
        int totalCost = item.getPointsCost() * quantity;
        pointsService.spendPoints(userId, totalCost, "exchange", itemId,
                "兑换: " + item.getName() + " x" + quantity);
        item.setStock(item.getStock() - quantity);
        exchangeItemMapper.updateById(item);
        ExchangeRecord record = new ExchangeRecord();
        record.setUserId(userId);
        record.setItemId(itemId);
        record.setQuantity(quantity);
        record.setPointsCost(totalCost);
        record.setDeliveryAddress(deliveryAddress);
        record.setStatus(0);
        exchangeRecordMapper.insert(record);
        return record;
    }

    public Page<ExchangeRecord> getExchangeRecords(Long userId, int page, int size) {
        return exchangeRecordMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<ExchangeRecord>()
                .eq(userId != null, ExchangeRecord::getUserId, userId)
                .orderByDesc(ExchangeRecord::getCreatedAt)
        );
    }

    public void confirmReceipt(Long id, Long userId) {
        ExchangeRecord record = exchangeRecordMapper.selectById(id);
        if (record == null) throw new BusinessException("兑换记录不存在");
        if (!record.getUserId().equals(userId)) throw new BusinessException("无权操作");
        if (record.getStatus() != 1) throw new BusinessException("请等待发货");
        record.setStatus(2);
        exchangeRecordMapper.updateById(record);
    }
}
