package com.charity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charity.entity.Donation;
import com.charity.entity.Item;
import com.charity.exception.BusinessException;
import com.charity.mapper.DonationMapper;
import com.charity.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationMapper donationMapper;
    private final ItemMapper itemMapper;
    private final PointsService pointsService;

    public Donation publish(Donation donation) {
        donation.setStatus(0);
        donationMapper.insert(donation);
        return donation;
    }

    public Page<Donation> list(Long userId, Integer status, int page, int size) {
        LambdaQueryWrapper<Donation> wrapper = new LambdaQueryWrapper<Donation>()
                .eq(userId != null, Donation::getUserId, userId)
                .eq(status != null, Donation::getStatus, status)
                .orderByDesc(Donation::getCreatedAt);
        return donationMapper.selectPage(new Page<>(page, size), wrapper);
    }

    public Donation getDetail(Long id) {
        Donation d = donationMapper.selectById(id);
        if (d == null) throw new BusinessException("捐赠记录不存在");
        return d;
    }

    public Donation update(Long id, Long userId, Donation updated) {
        Donation d = getDetail(id);
        if (!d.getUserId().equals(userId)) throw new BusinessException("无权操作");
        if (d.getStatus() != 0) throw new BusinessException("只有待审核的捐赠可以修改");
        updated.setId(id);
        updated.setUserId(userId);
        donationMapper.updateById(updated);
        return donationMapper.selectById(id);
    }

    public void delete(Long id, Long userId) {
        Donation d = getDetail(id);
        if (!d.getUserId().equals(userId)) throw new BusinessException("无权操作");
        if (d.getStatus() != 0 && d.getStatus() != 2) throw new BusinessException("只有待审核或已拒绝的捐赠可以删除");
        donationMapper.deleteById(id);
    }

    /** 审核通过：更新状态，发放积分，生成可申领物资 */
    public void approve(Long id, Long auditorId) {
        Donation d = getDetail(id);
        if (d.getStatus() != 0) throw new BusinessException("状态不正确");
        d.setStatus(1);
        int points = calculatePoints(d.getCategory(), d.getConditionLevel(), d.getQuantity());
        d.setPointsAwarded(points);
        donationMapper.updateById(d);
        pointsService.addPoints(d.getUserId(), points, "donate", id, "捐赠奖励: " + d.getTitle());
        Item item = new Item();
        item.setDonationId(id);
        item.setTitle(d.getTitle());
        item.setCategory(d.getCategory());
        item.setQuantity(d.getQuantity());
        item.setRemaining(d.getQuantity());
        item.setUnit(d.getUnit());
        item.setDescription(d.getDescription());
        item.setImages(d.getImages());
        itemMapper.insert(item);
    }

    public void reject(Long id, String reason) {
        Donation d = getDetail(id);
        if (d.getStatus() != 0) throw new BusinessException("状态不正确");
        d.setStatus(2);
        d.setRejectReason(reason);
        donationMapper.updateById(d);
    }

    /** 按品类+成色+数量计算积分
     * 主粮/零食：每单位10分；玩具/窝垫：每件5分；药品/洗护：每件8分；其他：每件3分
     * 成色系数：全新1.0、九成新0.9、八成新0.8、七成新0.6
     */
    private int calculatePoints(String category, String conditionLevel, Integer quantity) {
        if (quantity == null || quantity <= 0) quantity = 1;
        int basePerUnit;
        switch (category == null ? "" : category) {
            case "pet_food":
            case "pet_snack":   basePerUnit = 10; break;
            case "pet_toy":
            case "pet_bed":     basePerUnit = 5;  break;
            case "pet_medicine":
            case "pet_care":    basePerUnit = 8;  break;
            default:            basePerUnit = 3;  break;
        }
        double conditionFactor;
        switch (conditionLevel == null ? "new" : conditionLevel) {
            case "new":       conditionFactor = 1.0; break;
            case "like_new":  conditionFactor = 0.9; break;
            case "good":      conditionFactor = 0.8; break;
            case "fair":      conditionFactor = 0.6; break;
            default:          conditionFactor = 1.0; break;
        }
        return Math.min((int)(basePerUnit * conditionFactor * quantity), 500);
    }
}
