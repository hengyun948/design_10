package com.property.repair.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.repair.dto.RepairAssignDTO;
import com.property.repair.dto.RepairOrderAddDTO;
import com.property.repair.dto.RepairOrderQueryDTO;
import com.property.repair.entity.RepairOrder;
import com.property.repair.vo.RepairOrderVO;
import com.property.repair.vo.RepairProcessVO;

import java.util.List;

public interface RepairOrderService extends IService<RepairOrder> {
    PageResult<RepairOrderVO> page(RepairOrderQueryDTO dto);
    PageResult<RepairOrderVO> myOrders(RepairOrderQueryDTO dto);
    PageResult<RepairOrderVO> assignedOrders(RepairOrderQueryDTO dto);
    RepairOrderVO getDetail(Long id);
    List<RepairProcessVO> getRecords(Long id);
    Long submit(RepairOrderAddDTO dto, Long ownerId);
    void accept(Long id, Long operatorId);
    void assign(Long id, RepairAssignDTO dto, Long operatorId);
    void receive(Long id, Long workerId);
    void finish(Long id, Long workerId, String remark);
    void cancel(Long id, Long operatorId, String reason);
}
