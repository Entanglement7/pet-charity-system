package com.charity.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charity.entity.Organization;
import com.charity.entity.OrganizationAddress;
import com.charity.exception.BusinessException;
import com.charity.mapper.OrganizationAddressMapper;
import com.charity.mapper.OrganizationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationMapper organizationMapper;
    private final OrganizationAddressMapper addressMapper;

    public Organization register(Organization org) {
        Organization existing = organizationMapper.findByUserId(org.getUserId());
        if (existing != null) {
            throw new BusinessException("该用户已注册机构");
        }
        org.setStatus(0);
        organizationMapper.insert(org);
        return org;
    }

    public Organization getByUserId(Long userId) {
        Organization org = organizationMapper.findByUserId(userId);
        if (org == null) {
            throw new BusinessException("机构信息不存在");
        }
        return org;
    }

    public Organization findByUserId(Long userId) {
        return organizationMapper.findByUserId(userId);
    }

    public Organization update(Long userId, Organization updated) {
        Organization org = getByUserId(userId);
        updated.setId(org.getId());
        updated.setUserId(userId);
        updated.setStatus(org.getStatus());
        organizationMapper.updateById(updated);
        return organizationMapper.selectById(org.getId());
    }

    public List<OrganizationAddress> getAddressList(Long organizationId) {
        return addressMapper.selectList(
            new LambdaQueryWrapper<OrganizationAddress>()
                .eq(OrganizationAddress::getOrganizationId, organizationId)
        );
    }

    public OrganizationAddress addAddress(OrganizationAddress address) {
        addressMapper.insert(address);
        return address;
    }

    public OrganizationAddress updateAddress(Long id, Long organizationId, OrganizationAddress updated) {
        OrganizationAddress existing = addressMapper.selectById(id);
        if (existing == null || !existing.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("地址不存在");
        }
        updated.setId(id);
        updated.setOrganizationId(organizationId);
        addressMapper.updateById(updated);
        return addressMapper.selectById(id);
    }

    public void deleteAddress(Long id, Long organizationId) {
        OrganizationAddress existing = addressMapper.selectById(id);
        if (existing == null || !existing.getOrganizationId().equals(organizationId)) {
            throw new BusinessException("地址不存在");
        }
        addressMapper.deleteById(id);
    }
}
