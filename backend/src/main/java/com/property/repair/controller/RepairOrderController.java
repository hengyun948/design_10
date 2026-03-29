package com.property.repair.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.repair.dto.RepairAssignDTO;
import com.property.repair.dto.RepairOrderAddDTO;
import com.property.repair.dto.RepairOrderQueryDTO;
import com.property.repair.service.RepairOrderService;
import com.property.repair.vo.RepairOrderVO;
import com.property.repair.vo.RepairProcessVO;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repairs")
@RequiredArgsConstructor
public class RepairOrderController {

    private final RepairOrderService repairOrderService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<RepairOrderVO>> page(RepairOrderQueryDTO dto) {
        return Result.success(repairOrderService.page(dto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('OWNER')")
    public Result<PageResult<RepairOrderVO>> myOrders(RepairOrderQueryDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        dto.setOwnerId(userId);
        return Result.success(repairOrderService.myOrders(dto));
    }

    @GetMapping("/assigned")
    @PreAuthorize("hasAuthority('REPAIR_WORKER')")
    public Result<PageResult<RepairOrderVO>> assignedOrders(RepairOrderQueryDTO dto) {
        Long workerId = SecurityUtils.getCurrentUserId();
        dto.setAssignedWorkerId(workerId);
        return Result.success(repairOrderService.assignedOrders(dto));
    }

    @GetMapping("/{id}")
    public Result<RepairOrderVO> getDetail(@PathVariable Long id) {
        return Result.success(repairOrderService.getDetail(id));
    }

    @GetMapping("/{id}/records")
    public Result<List<RepairProcessVO>> getRecords(@PathVariable Long id) {
        return Result.success(repairOrderService.getRecords(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OWNER')")
    public Result<Long> submit(@Valid @RequestBody RepairOrderAddDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(repairOrderService.submit(dto, userId));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> accept(@PathVariable Long id) {
        repairOrderService.accept(id, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> assign(@PathVariable Long id, @Valid @RequestBody RepairAssignDTO dto) {
        repairOrderService.assign(id, dto, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAuthority('REPAIR_WORKER')")
    public Result<Void> receive(@PathVariable Long id) {
        repairOrderService.receive(id, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasAuthority('REPAIR_WORKER')")
    public Result<Void> finish(@PathVariable Long id, @RequestBody(required = false) ActionRemarkRequest req) {
        String remark = req != null ? req.getRemark() : null;
        repairOrderService.finish(id, SecurityUtils.getCurrentUserId(), remark);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, @RequestBody(required = false) ActionRemarkRequest req) {
        String reason = req != null ? req.getRemark() : null;
        repairOrderService.cancel(id, SecurityUtils.getCurrentUserId(), reason);
        return Result.success();
    }

    @Data
    static class ActionRemarkRequest {
        private String remark;
    }
}
