package com.property.bill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.bill.dto.BillAddDTO;
import com.property.bill.dto.BillQueryDTO;
import com.property.bill.entity.BillInfo;
import com.property.bill.vo.BillVO;
import com.property.common.model.PageResult;

import java.util.List;

public interface BillInfoService extends IService<BillInfo> {
    PageResult<BillVO> page(BillQueryDTO dto);
    PageResult<BillVO> myBills(BillQueryDTO dto, Long userId);
    BillVO getDetail(Long id);
    void add(BillAddDTO dto);
    void pay(Long billId, Long operatorId);
    List<Object> getPaymentRecords(Long billId);
    void delete(Long id);
}
