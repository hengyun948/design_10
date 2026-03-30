package com.property.inspection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.inspection.dto.FacilityAddDTO;
import com.property.inspection.dto.FacilityQueryDTO;
import com.property.inspection.entity.FacilityInfo;

public interface FacilityService extends IService<FacilityInfo> {
    PageResult<FacilityInfo> page(FacilityQueryDTO dto);
    Long add(FacilityAddDTO dto);
    void update(Long id, FacilityAddDTO dto);
    void delete(Long id);
}
