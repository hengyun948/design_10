package com.property.log.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import lombok.Data;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/logs")
public class OperationLogController {

    @GetMapping("/operations")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<PageResult<OperationLogVO>> pageOperationLogs(
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String moduleName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        PageResult<OperationLogVO> result = new PageResult<>();
        result.setTotal(0);
        result.setRecords(Collections.emptyList());
        return Result.success(result);
    }

    @Data
    static class OperationLogVO {
        private String moduleName;
        private String operationType;
        private String requestUri;
        private String operatorName;
        private String ip;
        private Long executeTime;
        private Integer status;
        private String createTime;
    }
}
