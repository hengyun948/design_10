package com.property.inspection.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.inspection.dto.InspectionAddDTO;
import com.property.inspection.dto.InspectionQueryDTO;
import com.property.inspection.entity.FacilityInfo;
import com.property.inspection.service.InspectionService;
import com.property.inspection.vo.InspectionVO;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<InspectionVO>> page(InspectionQueryDTO dto) {
        return Result.success(inspectionService.page(dto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('REPAIR_WORKER')")
    public Result<PageResult<InspectionVO>> myRecords(InspectionQueryDTO dto) {
        dto.setInspectorId(SecurityUtils.getCurrentUserId());
        return Result.success(inspectionService.myRecords(dto));
    }

    @GetMapping("/facilities")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF','REPAIR_WORKER')")
    public Result<List<FacilityInfo>> listFacilities() {
        return Result.success(inspectionService.listFacilities());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('REPAIR_WORKER')")
    public Result<Long> add(@Valid @RequestBody InspectionAddDTO dto) {
        return Result.success(inspectionService.add(dto, SecurityUtils.getCurrentUserId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        inspectionService.delete(id);
        return Result.success();
    }
}
