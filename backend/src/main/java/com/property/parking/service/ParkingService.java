package com.property.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.parking.dto.ParkingRecordAddDTO;
import com.property.parking.dto.ParkingRecordQueryDTO;
import com.property.parking.dto.ParkingSpaceAddDTO;
import com.property.parking.dto.PayParkingRecordDTO;
import com.property.parking.dto.ParkingSpaceQueryDTO;
import com.property.parking.entity.ParkingSpace;
import com.property.parking.vo.ParkingRecordVO;
import com.property.parking.vo.ParkingSpaceVO;

public interface ParkingService extends IService<ParkingSpace> {
    PageResult<ParkingSpaceVO> pageSpaces(ParkingSpaceQueryDTO dto);
    Long addSpace(ParkingSpaceAddDTO dto);
    void updateSpace(Long id, ParkingSpaceAddDTO dto);
    void deleteSpace(Long id);

    PageResult<ParkingRecordVO> pageRecords(ParkingRecordQueryDTO dto);
    PageResult<ParkingRecordVO> myRecords(ParkingRecordQueryDTO dto, Long ownerUserId);
    Long addRecord(ParkingRecordAddDTO dto);
    void pay(Long recordId, PayParkingRecordDTO dto);
}
