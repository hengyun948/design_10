package com.property.log.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.log.service.OperationLogService;
import com.property.log.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping("/operations")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<PageResult<OperationLogVO>> pageOperationLogs(
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String moduleName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return Result.success(operationLogService.page(operatorName, moduleName, status, pageNum, pageSize));
    }
}
