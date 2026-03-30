package com.property.parking.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.parking.dto.ParkingRecordAddDTO;
import com.property.parking.dto.ParkingRecordQueryDTO;
import com.property.parking.dto.ParkingSpaceAddDTO;
import com.property.parking.dto.PayParkingRecordDTO;
import com.property.parking.dto.ParkingSpaceQueryDTO;
import com.property.parking.service.ParkingService;
import com.property.parking.vo.ParkingRecordVO;
import com.property.parking.vo.ParkingSpaceVO;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    // ===== 车位 =====
    @GetMapping("/spaces")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<ParkingSpaceVO>> pageSpaces(ParkingSpaceQueryDTO dto) {
        return Result.success(parkingService.pageSpaces(dto));
    }

    @PostMapping("/spaces")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Long> addSpace(@Valid @RequestBody ParkingSpaceAddDTO dto) {
        return Result.success(parkingService.addSpace(dto));
    }

    @PutMapping("/spaces/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> updateSpace(@PathVariable Long id, @Valid @RequestBody ParkingSpaceAddDTO dto) {
        parkingService.updateSpace(id, dto);
        return Result.success();
    }

    @DeleteMapping("/spaces/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> deleteSpace(@PathVariable Long id) {
        parkingService.deleteSpace(id);
        return Result.success();
    }

    // ===== 停车记录 =====
    @GetMapping("/records")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<ParkingRecordVO>> pageRecords(ParkingRecordQueryDTO dto) {
        return Result.success(parkingService.pageRecords(dto));
    }

    @PostMapping("/records")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Long> addRecord(@Valid @RequestBody ParkingRecordAddDTO dto) {
        return Result.success(parkingService.addRecord(dto));
    }

    @GetMapping("/records/my")
    @PreAuthorize("hasAuthority('OWNER')")
    public Result<PageResult<ParkingRecordVO>> myRecords(ParkingRecordQueryDTO dto) {
        return Result.success(parkingService.myRecords(dto, SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/records/{id}/pay")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> pay(@PathVariable Long id, @Valid @RequestBody PayParkingRecordDTO dto) {
        parkingService.pay(id, dto);
        return Result.success();
    }
}
