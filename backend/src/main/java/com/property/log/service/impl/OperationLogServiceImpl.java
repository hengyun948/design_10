package com.property.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.log.entity.OperationLog;
import com.property.log.mapper.OperationLogMapper;
import com.property.log.service.OperationLogService;
import com.property.log.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public PageResult<OperationLogVO> page(String operatorName, String moduleName, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .like(StringUtils.hasText(operatorName), OperationLog::getOperatorName, operatorName)
                .like(StringUtils.hasText(moduleName), OperationLog::getModuleName, moduleName)
                .eq(status != null, OperationLog::getStatus, status)
                .orderByDesc(OperationLog::getCreateTime);
        Page<OperationLog> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    @Async
    public void saveLog(OperationLog log) {
        this.save(log);
    }

    private OperationLogVO toVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        vo.setId(log.getId());
        vo.setModuleName(log.getModuleName());
        vo.setOperationType(log.getOperationType());
        vo.setRequestUri(log.getRequestUri());
        vo.setRequestMethod(log.getRequestMethod());
        vo.setOperatorId(log.getOperatorId());
        vo.setOperatorName(log.getOperatorName());
        vo.setOperationDesc(log.getOperationDesc());
        vo.setIp(log.getIp());
        vo.setExecuteTime(log.getExecuteTime());
        vo.setStatus(log.getStatus());
        vo.setErrorMsg(log.getErrorMsg());
        vo.setCreateTime(log.getCreateTime());
        return vo;
    }
}
