package com.property.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.inspection.dto.FacilityAddDTO;
import com.property.inspection.dto.FacilityQueryDTO;
import com.property.inspection.entity.FacilityInfo;
import com.property.inspection.entity.InspectionRecord;
import com.property.inspection.mapper.FacilityInfoMapper;
import com.property.inspection.mapper.InspectionRecordMapper;
import com.property.inspection.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl extends ServiceImpl<FacilityInfoMapper, FacilityInfo> implements FacilityService {

    private final InspectionRecordMapper inspectionRecordMapper;

    @Override
    public PageResult<FacilityInfo> page(FacilityQueryDTO dto) {
        LambdaQueryWrapper<FacilityInfo> wrapper = new LambdaQueryWrapper<FacilityInfo>()
                .eq(StringUtils.hasText(dto.getFacilityType()), FacilityInfo::getFacilityType, dto.getFacilityType())
                .and(StringUtils.hasText(dto.getKeyword()), w ->
                        w.like(FacilityInfo::getFacilityName, dto.getKeyword())
                         .or().like(FacilityInfo::getLocation, dto.getKeyword()))
                .orderByAsc(FacilityInfo::getFacilityType)
                .orderByAsc(FacilityInfo::getFacilityName);
        List<FacilityInfo> records = this.list(wrapper);
        for (FacilityInfo facility : records) {
            InspectionRecord latestRecord = inspectionRecordMapper.selectOne(
                    new LambdaQueryWrapper<InspectionRecord>()
                            .eq(InspectionRecord::getFacilityId, facility.getId())
                            .orderByDesc(InspectionRecord::getInspectionTime)
                            .orderByDesc(InspectionRecord::getId)
                            .last("limit 1"));
            if (latestRecord != null) {
                facility.setStatus("NORMAL".equals(latestRecord.getResult()) ? 1 : 0);
                facility.setRemark(latestRecord.getRemark());
            }
        }
        List<FacilityInfo> filteredRecords = records.stream()
                .filter(facility -> dto.getStatus() == null || dto.getStatus().equals(facility.getStatus()))
                .collect(Collectors.toList());
        int fromIndex = Math.max((dto.getPageNum() - 1) * dto.getPageSize(), 0);
        int toIndex = Math.min(fromIndex + dto.getPageSize(), filteredRecords.size());
        PageResult<FacilityInfo> result = new PageResult<>();
        result.setTotal(filteredRecords.size());
        result.setRecords(fromIndex >= filteredRecords.size()
                ? Collections.emptyList()
                : filteredRecords.subList(fromIndex, toIndex));
        return result;
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
