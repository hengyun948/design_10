package com.property.inspection.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.inspection.dto.FacilityAddDTO;
import com.property.inspection.dto.FacilityQueryDTO;
import com.property.inspection.entity.FacilityInfo;
import com.property.inspection.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<FacilityInfo>> page(FacilityQueryDTO dto) {
        return Result.success(facilityService.page(dto));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Long> add(@Valid @RequestBody FacilityAddDTO dto) {
        return Result.success(facilityService.add(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FacilityAddDTO dto) {
        facilityService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        facilityService.delete(id);
        return Result.success();
    }
}
