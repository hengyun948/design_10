package com.property.house.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.house.dto.HouseAddDTO;
import com.property.house.dto.HouseQueryDTO;
import com.property.house.entity.HouseInfo;
import com.property.house.vo.HouseVO;

import java.util.List;

public interface HouseInfoService extends IService<HouseInfo> {
    PageResult<HouseVO> page(HouseQueryDTO dto);
    HouseVO getDetail(Long id);
    void add(HouseAddDTO dto);
    void update(Long id, HouseAddDTO dto);
    void delete(Long id);
    List<HouseVO> listAll();
}
