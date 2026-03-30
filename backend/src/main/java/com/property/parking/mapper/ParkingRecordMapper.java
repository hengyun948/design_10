package com.property.parking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.property.parking.entity.ParkingRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParkingRecordMapper extends BaseMapper<ParkingRecord> {
}
