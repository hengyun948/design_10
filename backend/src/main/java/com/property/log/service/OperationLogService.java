package com.property.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.log.entity.OperationLog;
import com.property.log.vo.OperationLogVO;

public interface OperationLogService extends IService<OperationLog> {
    PageResult<OperationLogVO> page(String operatorName, String moduleName, Integer status, Integer pageNum, Integer pageSize);
    void saveLog(OperationLog log);
}
