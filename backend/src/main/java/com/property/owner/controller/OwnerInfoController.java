package com.property.owner.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.owner.dto.BindHouseDTO;
import com.property.owner.dto.OwnerQueryDTO;
import com.property.owner.dto.UpdateOwnerProfileDTO;
import com.property.owner.service.OwnerInfoService;
import com.property.owner.vo.OwnerVO;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerInfoController {

    private final OwnerInfoService ownerInfoService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<OwnerVO>> page(OwnerQueryDTO dto) {
        return Result.success(ownerInfoService.page(dto));
    }

    @GetMapping("/my")
    public Result<OwnerVO> myInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(ownerInfoService.getByUserId(userId));
    }

    @GetMapping("/{userId}/info")
    public Result<OwnerVO> getByUserId(@PathVariable Long userId) {
        return Result.success(ownerInfoService.getByUserId(userId));
    }

    @GetMapping("/{ownerId}/houses")
    public Result<List<Object>> getHouses(@PathVariable Long ownerId) {
        return Result.success(ownerInfoService.getHousesByOwnerId(ownerId));
    }

    @PostMapping("/bind-house")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> bindHouse(@Valid @RequestBody BindHouseDTO dto) {
        ownerInfoService.bindHouse(dto);
        return Result.success();
    }

    @DeleteMapping("/unbind-house/{relId}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> unbindHouse(@PathVariable Long relId) {
        ownerInfoService.unbindHouse(relId);
        return Result.success();
    }

    @PutMapping("/{id}/profile")
    public Result<Void> updateProfile(@PathVariable Long id, @RequestBody UpdateOwnerProfileDTO dto) {
        ownerInfoService.updateOwnerInfo(id, dto);
        return Result.success();
    }
}
