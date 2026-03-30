package com.property.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.inspection.dto.InspectionAddDTO;
import com.property.inspection.dto.InspectionQueryDTO;
import com.property.inspection.entity.FacilityInfo;
import com.property.inspection.entity.InspectionRecord;
import com.property.inspection.mapper.FacilityInfoMapper;
import com.property.inspection.mapper.InspectionRecordMapper;
import com.property.inspection.service.InspectionService;
import com.property.inspection.vo.InspectionVO;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InspectionServiceImpl extends ServiceImpl<InspectionRecordMapper, InspectionRecord> implements InspectionService {

    private final FacilityInfoMapper facilityInfoMapper;
    private final SysUserMapper sysUserMapper;

    private static final Map<String, String> FACILITY_TYPE_NAMES = Map.of(
            "ELEVATOR", "电梯", "FIRE", "消防", "WATER", "水务", "POWER", "供电", "OTHER", "其他"
    );

    @Override
    public PageResult<InspectionVO> page(InspectionQueryDTO dto) {
        LambdaQueryWrapper<InspectionRecord> wrapper = buildWrapper(dto);
        Page<InspectionRecord> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public PageResult<InspectionVO> myRecords(InspectionQueryDTO dto) {
        LambdaQueryWrapper<InspectionRecord> wrapper = buildWrapper(dto)
                .eq(InspectionRecord::getInspectorId, dto.getInspectorId());
        Page<InspectionRecord> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public Long add(InspectionAddDTO dto, Long inspectorId) {
        FacilityInfo facility = facilityInfoMapper.selectById(dto.getFacilityId());
        if (facility == null) throw new BusinessException("设施不存在");
        InspectionRecord record = new InspectionRecord();
        record.setFacilityId(dto.getFacilityId());
        record.setInspectorId(inspectorId);
        record.setInspectionTime(dto.getInspectionTime());
        record.setResult(dto.getResult() != null ? dto.getResult() : "NORMAL");
        record.setStatus(1);
        record.setRemark(dto.getRemark());
        save(record);
        return record.getId();
    }

    @Override
    public void delete(Long id) {
        if (!removeById(id)) throw new BusinessException("删除失败");
    }

    @Override
    public List<FacilityInfo> listFacilities() {
        return facilityInfoMapper.selectList(
                new LambdaQueryWrapper<FacilityInfo>().orderByAsc(FacilityInfo::getFacilityType));
    }

    private LambdaQueryWrapper<InspectionRecord> buildWrapper(InspectionQueryDTO dto) {
        return new LambdaQueryWrapper<InspectionRecord>()
                .eq(dto.getFacilityId() != null, InspectionRecord::getFacilityId, dto.getFacilityId())
                .eq(StringUtils.hasText(dto.getResult()), InspectionRecord::getResult, dto.getResult())
                .orderByDesc(InspectionRecord::getInspectionTime);
    }

    private InspectionVO toVO(InspectionRecord r) {
        InspectionVO vo = new InspectionVO();
        vo.setId(r.getId());
        vo.setFacilityId(r.getFacilityId());
        vo.setInspectorId(r.getInspectorId());
        vo.setInspectionTime(r.getInspectionTime());
        vo.setResult(r.getResult());
        vo.setResultName("NORMAL".equals(r.getResult()) ? "正常" : "异常");
        vo.setStatus(r.getStatus());
        vo.setRemark(r.getRemark());
        vo.setCreateTime(r.getCreateTime());
        // 设施信息
        FacilityInfo facility = facilityInfoMapper.selectById(r.getFacilityId());
        if (facility != null) {
            vo.setFacilityName(facility.getFacilityName());
            vo.setFacilityType(facility.getFacilityType());
            vo.setFacilityTypeName(FACILITY_TYPE_NAMES.getOrDefault(facility.getFacilityType(), facility.getFacilityType()));
            vo.setFacilityLocation(facility.getLocation());
        }
        // 巡检人
        SysUser inspector = sysUserMapper.selectById(r.getInspectorId());
        vo.setInspectorName(inspector != null ? inspector.getRealName() : "");
        return vo;
    }
}
