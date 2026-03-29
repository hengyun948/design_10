package com.property.house.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.house.dto.HouseAddDTO;
import com.property.house.dto.HouseQueryDTO;
import com.property.house.service.HouseInfoService;
import com.property.house.vo.HouseVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/houses")
@RequiredArgsConstructor
public class HouseInfoController {

    private final HouseInfoService houseInfoService;

    @GetMapping
    public Result<PageResult<HouseVO>> page(HouseQueryDTO dto) {
        return Result.success(houseInfoService.page(dto));
    }

    @GetMapping("/all")
    public Result<List<HouseVO>> listAll() {
        return Result.success(houseInfoService.listAll());
    }

    @GetMapping("/{id}")
    public Result<HouseVO> detail(@PathVariable Long id) {
        return Result.success(houseInfoService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> add(@Valid @RequestBody HouseAddDTO dto) {
        houseInfoService.add(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody HouseAddDTO dto) {
        houseInfoService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        houseInfoService.delete(id);
        return Result.success();
    }
}
