package com.property.owner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.house.entity.HouseInfo;
import com.property.house.mapper.HouseInfoMapper;
import com.property.owner.dto.BindHouseDTO;
import com.property.owner.dto.OwnerQueryDTO;
import com.property.owner.dto.UpdateOwnerProfileDTO;
import com.property.owner.entity.OwnerHouseRel;
import com.property.owner.entity.OwnerInfo;
import com.property.owner.mapper.OwnerHouseRelMapper;
import com.property.owner.mapper.OwnerInfoMapper;
import com.property.owner.service.OwnerInfoService;
import com.property.owner.vo.OwnerHouseVO;
import com.property.owner.vo.OwnerVO;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerInfoServiceImpl extends ServiceImpl<OwnerInfoMapper, OwnerInfo> implements OwnerInfoService {

    private final SysUserMapper sysUserMapper;
    private final OwnerHouseRelMapper ownerHouseRelMapper;
    private final HouseInfoMapper houseInfoMapper;

    @Override
    public PageResult<OwnerVO> page(OwnerQueryDTO dto) {
        // 先从 sys_user 中查 OWNER 角色用户
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRoleCode, "OWNER")
                .eq(SysUser::getDeleted, 0)
                .and(StringUtils.hasText(dto.getKeyword()), w -> w
                        .like(SysUser::getRealName, dto.getKeyword())
                        .or().like(SysUser::getPhone, dto.getKeyword())
                        .or().like(SysUser::getUsername, dto.getKeyword()))
                .orderByDesc(SysUser::getCreateTime);
        Page<SysUser> userPage = sysUserMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), userWrapper);

        List<Long> userIds = userPage.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
        Map<Long, OwnerInfo> ownerInfoMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            list(new LambdaQueryWrapper<OwnerInfo>().in(OwnerInfo::getUserId, userIds))
                    .forEach(o -> ownerInfoMap.put(o.getUserId(), o));
        }
        // 自动为没有 owner_info 记录的 OWNER 用户创建
        List<OwnerInfo> toCreate = new ArrayList<>();
        for (SysUser u : userPage.getRecords()) {
            if (!ownerInfoMap.containsKey(u.getId())) {
                OwnerInfo newInfo = new OwnerInfo();
                newInfo.setUserId(u.getId());
                toCreate.add(newInfo);
            }
        }
        if (!toCreate.isEmpty()) {
            saveBatch(toCreate);
            toCreate.forEach(o -> ownerInfoMap.put(o.getUserId(), o));
        }

        return PageResult.of(userPage.convert(u -> toVO(u, ownerInfoMap.get(u.getId()))));
    }

    @Override
    public OwnerVO getByUserId(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        OwnerInfo info = getOne(new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, userId));
        return toVO(user, info);
    }

    @Override
    public List<Object> getHousesByOwnerId(Long ownerId) {
        List<OwnerHouseRel> rels = ownerHouseRelMapper.selectList(
                new LambdaQueryWrapper<OwnerHouseRel>().eq(OwnerHouseRel::getOwnerId, ownerId));
        if (rels.isEmpty()) return List.of();
        List<Long> houseIds = rels.stream().map(OwnerHouseRel::getHouseId).collect(Collectors.toList());
        Map<Long, HouseInfo> houseMap = houseInfoMapper.selectBatchIds(houseIds)
                .stream().collect(Collectors.toMap(HouseInfo::getId, h -> h));
        return rels.stream().map(rel -> {
            HouseInfo h = houseMap.get(rel.getHouseId());
            if (h == null) return null;
            OwnerHouseVO vo = new OwnerHouseVO();
            vo.setRelId(rel.getId());
            vo.setHouseId(h.getId());
            vo.setFullAddress(h.getBuildingNo() + "栋" + h.getUnitNo() + "单元" + h.getRoomNo() + "室");
            vo.setBuildingNo(h.getBuildingNo());
            vo.setUnitNo(h.getUnitNo());
            vo.setRoomNo(h.getRoomNo());
            vo.setArea(h.getArea());
            vo.setHouseType(h.getHouseType());
            vo.setHouseStatus(h.getHouseStatus());
            vo.setHouseStatusName(new String[]{"待售","已售","已租"}[h.getHouseStatus() != null ? h.getHouseStatus() : 0]);
            vo.setBindType(rel.getBindType());
            vo.setBindTypeName(rel.getBindType() == 1 ? "业主" : "租户");
            vo.setStartDate(rel.getStartDate());
            vo.setEndDate(rel.getEndDate());
            return (Object) vo;
        }).filter(v -> v != null).collect(Collectors.toList());
    }

    @Override
    public void bindHouse(BindHouseDTO dto) {
        long exists = ownerHouseRelMapper.selectCount(new LambdaQueryWrapper<OwnerHouseRel>()
                .eq(OwnerHouseRel::getOwnerId, dto.getOwnerId())
                .eq(OwnerHouseRel::getHouseId, dto.getHouseId()));
        if (exists > 0) throw new BusinessException("该业主已绑定此房屋");
        OwnerHouseRel rel = new OwnerHouseRel();
        rel.setOwnerId(dto.getOwnerId());
        rel.setHouseId(dto.getHouseId());
        rel.setBindType(dto.getBindType() != null ? dto.getBindType() : 1);
        rel.setIsPrimary(1);
        rel.setStartDate(dto.getStartDate());
        rel.setEndDate(dto.getEndDate());
        ownerHouseRelMapper.insert(rel);
        // 更新房屋状态为已售/已租
        HouseInfo house = houseInfoMapper.selectById(dto.getHouseId());
        if (house != null) {
            house.setHouseStatus(dto.getBindType() != null && dto.getBindType() == 2 ? 2 : 1);
            houseInfoMapper.updateById(house);
        }
    }

    @Override
    public void unbindHouse(Long relId) {
        OwnerHouseRel rel = ownerHouseRelMapper.selectById(relId);
        if (rel == null) throw new BusinessException("绑定关系不存在");
        ownerHouseRelMapper.deleteById(relId);
        HouseInfo house = houseInfoMapper.selectById(rel.getHouseId());
        if (house != null) {
            house.setHouseStatus(0);
            houseInfoMapper.updateById(house);
        }
    }

    @Override
    public void updateOwnerInfo(Long ownerId, UpdateOwnerProfileDTO dto) {
        OwnerInfo existing = getById(ownerId);
        if (existing == null) throw new BusinessException("业主信息不存在");
        existing.setGender(dto.getGender());
        existing.setIdCard(dto.getIdCard());
        existing.setAddress(dto.getAddress());
        existing.setRemark(dto.getRemark());
        updateById(existing);
        SysUser user = sysUserMapper.selectById(existing.getUserId());
        if (user != null) {
            if (dto.getRealName() != null) user.setRealName(dto.getRealName());
            if (dto.getPhone() != null) user.setPhone(dto.getPhone());
            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
            sysUserMapper.updateById(user);
        }
    }

    private OwnerVO toVO(SysUser user, OwnerInfo info) {
        OwnerVO vo = new OwnerVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        if (info != null) {
            vo.setId(info.getId());
            vo.setGender(info.getGender());
            vo.setGenderName(info.getGender() == null ? "" : info.getGender() == 1 ? "男" : "女");
            vo.setIdCard(info.getIdCard());
            vo.setAddress(info.getAddress());
            vo.setRemark(info.getRemark());
        }
        return vo;
    }
}
