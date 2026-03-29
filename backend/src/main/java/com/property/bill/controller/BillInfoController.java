package com.property.bill.controller;

import com.property.bill.dto.BillAddDTO;
import com.property.bill.dto.BillQueryDTO;
import com.property.bill.service.BillInfoService;
import com.property.bill.vo.BillVO;
import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillInfoController {

    private final BillInfoService billInfoService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<BillVO>> page(BillQueryDTO dto) {
        return Result.success(billInfoService.page(dto));
    }

    @GetMapping("/my")
    public Result<PageResult<BillVO>> myBills(BillQueryDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(billInfoService.myBills(dto, userId));
    }

    @GetMapping("/{id}")
    public Result<BillVO> detail(@PathVariable Long id) {
        return Result.success(billInfoService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> add(@Valid @RequestBody BillAddDTO dto) {
        billInfoService.add(dto);
        return Result.success();
    }

    @PostMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        billInfoService.pay(id, userId);
        return Result.success();
    }

    @GetMapping("/{id}/payments")
    public Result<List<Object>> payments(@PathVariable Long id) {
        return Result.success(billInfoService.getPaymentRecords(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        billInfoService.delete(id);
        return Result.success();
    }
}
