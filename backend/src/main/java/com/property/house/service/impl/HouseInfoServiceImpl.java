package com.property.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.house.dto.HouseAddDTO;
import com.property.house.dto.HouseQueryDTO;
import com.property.house.entity.HouseInfo;
import com.property.house.mapper.HouseInfoMapper;
import com.property.house.service.HouseInfoService;
import com.property.house.vo.HouseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseInfoServiceImpl extends ServiceImpl<HouseInfoMapper, HouseInfo> implements HouseInfoService {

    private static final String[] STATUS_NAMES = {"待售", "已售", "已租"};

    @Override
    public PageResult<HouseVO> page(HouseQueryDTO dto) {
        LambdaQueryWrapper<HouseInfo> wrapper = new LambdaQueryWrapper<HouseInfo>()
                .like(StringUtils.hasText(dto.getBuildingNo()), HouseInfo::getBuildingNo, dto.getBuildingNo())
                .like(StringUtils.hasText(dto.getUnitNo()), HouseInfo::getUnitNo, dto.getUnitNo())
                .like(StringUtils.hasText(dto.getRoomNo()), HouseInfo::getRoomNo, dto.getRoomNo())
                .eq(dto.getHouseStatus() != null, HouseInfo::getHouseStatus, dto.getHouseStatus())
                .orderByAsc(HouseInfo::getBuildingNo, HouseInfo::getUnitNo, HouseInfo::getRoomNo);
        Page<HouseInfo> result = baseMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(result.convert(this::toVO));
    }

    @Override
    public HouseVO getDetail(Long id) {
        HouseInfo house = getById(id);
        if (house == null) throw new BusinessException("房屋不存在");
        return toVO(house);
    }

    @Override
    public void add(HouseAddDTO dto) {
        long count = count(new LambdaQueryWrapper<HouseInfo>()
                .eq(HouseInfo::getBuildingNo, dto.getBuildingNo())
                .eq(HouseInfo::getUnitNo, dto.getUnitNo())
                .eq(HouseInfo::getRoomNo, dto.getRoomNo()));
        if (count > 0) throw new BusinessException("该房间已存在");
        HouseInfo house = new HouseInfo();
        house.setBuildingNo(dto.getBuildingNo());
        house.setUnitNo(dto.getUnitNo());
        house.setRoomNo(dto.getRoomNo());
        house.setArea(dto.getArea());
        house.setHouseType(dto.getHouseType());
        house.setHouseStatus(dto.getHouseStatus() != null ? dto.getHouseStatus() : 0);
        house.setRemark(dto.getRemark());
        save(house);
    }

    @Override
    public void update(Long id, HouseAddDTO dto) {
        HouseInfo house = getById(id);
        if (house == null) throw new BusinessException("房屋不存在");
        house.setArea(dto.getArea());
        house.setHouseType(dto.getHouseType());
        house.setHouseStatus(dto.getHouseStatus());
        house.setRemark(dto.getRemark());
        updateById(house);
    }

    @Override
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public List<HouseVO> listAll() {
        return list().stream().map(this::toVO).collect(Collectors.toList());
    }

    private HouseVO toVO(HouseInfo h) {
        HouseVO vo = new HouseVO();
        vo.setId(h.getId());
        vo.setBuildingNo(h.getBuildingNo());
        vo.setUnitNo(h.getUnitNo());
        vo.setRoomNo(h.getRoomNo());
        vo.setFullAddress(h.getBuildingNo() + "栋" + h.getUnitNo() + "单元" + h.getRoomNo() + "室");
        vo.setArea(h.getArea());
        vo.setHouseType(h.getHouseType());
        vo.setHouseStatus(h.getHouseStatus());
        vo.setHouseStatusName(h.getHouseStatus() != null && h.getHouseStatus() < STATUS_NAMES.length
                ? STATUS_NAMES[h.getHouseStatus()] : "");
        vo.setRemark(h.getRemark());
        vo.setCreateTime(h.getCreateTime());
        return vo;
    }
}
