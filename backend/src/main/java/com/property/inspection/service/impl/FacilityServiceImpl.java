package com.property.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.inspection.dto.FacilityAddDTO;
import com.property.inspection.dto.FacilityQueryDTO;
import com.property.inspection.entity.FacilityInfo;
import com.property.inspection.mapper.FacilityInfoMapper;
import com.property.inspection.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl extends ServiceImpl<FacilityInfoMapper, FacilityInfo> implements FacilityService {

    @Override
    public PageResult<FacilityInfo> page(FacilityQueryDTO dto) {
        LambdaQueryWrapper<FacilityInfo> wrapper = new LambdaQueryWrapper<FacilityInfo>()
                .eq(StringUtils.hasText(dto.getFacilityType()), FacilityInfo::getFacilityType, dto.getFacilityType())
                .eq(dto.getStatus() != null, FacilityInfo::getStatus, dto.getStatus())
                .and(StringUtils.hasText(dto.getKeyword()), w ->
                        w.like(FacilityInfo::getFacilityName, dto.getKeyword())
                         .or().like(FacilityInfo::getLocation, dto.getKeyword()))
                .orderByAsc(FacilityInfo::getFacilityType)
                .orderByAsc(FacilityInfo::getFacilityName);
        Page<FacilityInfo> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    public Long add(FacilityAddDTO dto) {
        FacilityInfo facility = new FacilityInfo();
        copyFrom(dto, facility);
        save(facility);
        return facility.getId();
    }

    @Override
    public void update(Long id, FacilityAddDTO dto) {
        FacilityInfo facility = getById(id);
        if (facility == null) throw new BusinessException("设施不存在");
        copyFrom(dto, facility);
        updateById(facility);
    }

    @Override
    public void delete(Long id) {
        if (!removeById(id)) throw new BusinessException("删除失败");
    }

    private void copyFrom(FacilityAddDTO dto, FacilityInfo facility) {
        facility.setFacilityName(dto.getFacilityName());
        facility.setFacilityType(dto.getFacilityType());
        facility.setLocation(dto.getLocation());
        facility.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        facility.setLastMaintenanceTime(dto.getLastMaintenanceTime());
        facility.setRemark(dto.getRemark());
    }
}
