package com.property.inspection.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.inspection.dto.InspectionAddDTO;
import com.property.inspection.dto.InspectionQueryDTO;
import com.property.inspection.entity.FacilityInfo;
import com.property.inspection.entity.InspectionRecord;
import com.property.inspection.vo.InspectionVO;

import java.util.List;

public interface InspectionService extends IService<InspectionRecord> {
    PageResult<InspectionVO> page(InspectionQueryDTO dto);
    PageResult<InspectionVO> myRecords(InspectionQueryDTO dto);
    Long add(InspectionAddDTO dto, Long inspectorId);
    void delete(Long id);
    List<FacilityInfo> listFacilities();
}
