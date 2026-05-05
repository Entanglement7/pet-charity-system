package com.charity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charity.entity.Points;
import com.charity.entity.PointsRecord;
import com.charity.exception.BusinessException;
import com.charity.mapper.PointsMapper;
import com.charity.mapper.PointsRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointsService {

    private final PointsMapper pointsMapper;
    private final PointsRecordMapper recordMapper;

    public Points getOrCreate(Long userId) {
        Points points = pointsMapper.findByUserId(userId);
        if (points == null) {
            points = new Points();
            points.setUserId(userId);
            points.setBalance(0);
            points.setTotalEarned(0);
            points.setTotalSpent(0);
            pointsMapper.insert(points);
        }
        return points;
    }

    @Transactional
    public void addPoints(Long userId, int amount, String type, Long refId, String description) {
        Points points = getOrCreate(userId);
        points.setBalance(points.getBalance() + amount);
        points.setTotalEarned(points.getTotalEarned() + amount);
        pointsMapper.updateById(points);
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setType(type);
        record.setRefId(refId);
        record.setDescription(description);
        recordMapper.insert(record);
    }

    @Transactional
    public void spendPoints(Long userId, int amount, String type, Long refId, String description) {
        Points points = getOrCreate(userId);
        if (points.getBalance() < amount) {
            throw new BusinessException("积分不足");
        }
        points.setBalance(points.getBalance() - amount);
        points.setTotalSpent(points.getTotalSpent() + amount);
        pointsMapper.updateById(points);
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setAmount(-amount);
        record.setType(type);
        record.setRefId(refId);
        record.setDescription(description);
        recordMapper.insert(record);
    }

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<PointsRecord> getRecords(
            Long userId, int page, int size) {
        return recordMapper.selectPage(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size),
            new LambdaQueryWrapper<PointsRecord>()
                .eq(PointsRecord::getUserId, userId)
                .orderByDesc(PointsRecord::getCreatedAt)
        );
    }

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Points> getUserPointsList(int page, int size) {
        return pointsMapper.selectPage(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size),
            new LambdaQueryWrapper<Points>()
                .orderByDesc(Points::getBalance)
        );
    }
}
