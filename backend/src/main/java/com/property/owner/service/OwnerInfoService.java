package com.property.owner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.owner.dto.BindHouseDTO;
import com.property.owner.dto.OwnerQueryDTO;
import com.property.owner.entity.OwnerInfo;
import com.property.owner.vo.OwnerVO;

import java.util.List;

public interface OwnerInfoService extends IService<OwnerInfo> {
    PageResult<OwnerVO> page(OwnerQueryDTO dto);
    OwnerVO getByUserId(Long userId);
    List<Object> getHousesByOwnerId(Long ownerId);
    void bindHouse(BindHouseDTO dto);
    void unbindHouse(Long relId);
    void updateOwnerInfo(Long ownerId, OwnerInfo info);
}
