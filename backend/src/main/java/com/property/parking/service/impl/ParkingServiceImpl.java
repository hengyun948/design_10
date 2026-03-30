package com.property.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.owner.entity.OwnerInfo;
import com.property.owner.mapper.OwnerInfoMapper;
import com.property.parking.dto.ParkingRecordAddDTO;
import com.property.parking.dto.ParkingRecordQueryDTO;
import com.property.parking.dto.ParkingSpaceAddDTO;
import com.property.parking.dto.PayParkingRecordDTO;
import com.property.parking.dto.ParkingSpaceQueryDTO;
import com.property.parking.entity.ParkingRecord;
import com.property.parking.entity.ParkingSpace;
import com.property.parking.mapper.ParkingRecordMapper;
import com.property.parking.mapper.ParkingSpaceMapper;
import com.property.parking.service.ParkingService;
import com.property.parking.vo.ParkingRecordVO;
import com.property.parking.vo.ParkingSpaceVO;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl extends ServiceImpl<ParkingSpaceMapper, ParkingSpace> implements ParkingService {

    private final ParkingRecordMapper recordMapper;
    private final SysUserMapper sysUserMapper;
    private final OwnerInfoMapper ownerInfoMapper;

    // ===== Space =====
    @Override
    public PageResult<ParkingSpaceVO> pageSpaces(ParkingSpaceQueryDTO dto) {
        LambdaQueryWrapper<ParkingSpace> wrapper = new LambdaQueryWrapper<ParkingSpace>()
                .eq(StringUtils.hasText(dto.getSpaceType()), ParkingSpace::getSpaceType, dto.getSpaceType())
                .eq(dto.getStatus() != null, ParkingSpace::getStatus, dto.getStatus())
                .and(StringUtils.hasText(dto.getKeyword()), w ->
                        w.like(ParkingSpace::getSpaceNo, dto.getKeyword())
                         .or().like(ParkingSpace::getLocation, dto.getKeyword()))
                .orderByAsc(ParkingSpace::getSpaceNo);
        Page<ParkingSpace> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toSpaceVO));
    }

    @Override
    public Long addSpace(ParkingSpaceAddDTO dto) {
        Long exist = this.count(new LambdaQueryWrapper<ParkingSpace>().eq(ParkingSpace::getSpaceNo, dto.getSpaceNo()));
        if (exist > 0) throw new BusinessException("车位编号已存在");
        ParkingSpace space = new ParkingSpace();
        copySpaceFrom(dto, space);
        save(space);
        return space.getId();
    }

    @Override
    public void updateSpace(Long id, ParkingSpaceAddDTO dto) {
        ParkingSpace space = getById(id);
        if (space == null) throw new BusinessException("车位不存在");
        copySpaceFrom(dto, space);
        updateById(space);
    }

    @Override
    public void deleteSpace(Long id) {
        if (!removeById(id)) throw new BusinessException("删除失败");
    }

    // ===== Record =====
    @Override
    public PageResult<ParkingRecordVO> pageRecords(ParkingRecordQueryDTO dto) {
        Page<ParkingRecord> page = recordMapper.selectPage(
                new Page<>(dto.getPageNum(), dto.getPageSize()), buildRecordWrapper(dto));
        return PageResult.of(page.convert(this::toRecordVO));
    }

    @Override
    public PageResult<ParkingRecordVO> myRecords(ParkingRecordQueryDTO dto, Long ownerUserId) {
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, ownerUserId));
        if (ownerInfo == null) {
            Page<ParkingRecord> empty = new Page<>(dto.getPageNum(), dto.getPageSize());
            empty.setTotal(0);
            return PageResult.of(empty.convert(this::toRecordVO));
        }
        dto.setOwnerId(ownerInfo.getId());
        Page<ParkingRecord> page = recordMapper.selectPage(
                new Page<>(dto.getPageNum(), dto.getPageSize()), buildRecordWrapper(dto));
        return PageResult.of(page.convert(this::toRecordVO));
    }

    @Override
    public Long addRecord(ParkingRecordAddDTO dto) {
        if (getById(dto.getSpaceId()) == null) throw new BusinessException("车位不存在");
        ParkingRecord record = new ParkingRecord();
        record.setSpaceId(dto.getSpaceId());
        record.setCarNo(dto.getCarNo());
        record.setOwnerId(dto.getOwnerId());
        record.setStartTime(dto.getStartTime());
        record.setEndTime(dto.getEndTime());
        record.setFeeAmount(dto.getFeeAmount());
        record.setPayStatus(dto.getPayStatus() != null ? dto.getPayStatus() : 0);
        recordMapper.insert(record);
        return record.getId();
    }

    @Override
    public void pay(Long recordId, PayParkingRecordDTO dto) {
        ParkingRecord record = recordMapper.selectById(recordId);
        if (record == null) throw new BusinessException("记录不存在");
        if (record.getPayStatus() == 1) throw new BusinessException("已缴费，无需重复操作");
        record.setEndTime(dto.getEndTime());
        record.setFeeAmount(dto.getFeeAmount());
        record.setPayStatus(1);
        recordMapper.updateById(record);
    }

    private LambdaQueryWrapper<ParkingRecord> buildRecordWrapper(ParkingRecordQueryDTO dto) {
        return new LambdaQueryWrapper<ParkingRecord>()
                .eq(StringUtils.hasText(dto.getCarNo()), ParkingRecord::getCarNo, dto.getCarNo())
                .eq(dto.getSpaceId() != null, ParkingRecord::getSpaceId, dto.getSpaceId())
                .eq(dto.getOwnerId() != null, ParkingRecord::getOwnerId, dto.getOwnerId())
                .eq(dto.getPayStatus() != null, ParkingRecord::getPayStatus, dto.getPayStatus())
                .orderByDesc(ParkingRecord::getStartTime);
    }

    private ParkingSpaceVO toSpaceVO(ParkingSpace s) {
        ParkingSpaceVO vo = new ParkingSpaceVO();
        vo.setId(s.getId());
        vo.setSpaceNo(s.getSpaceNo());
        vo.setSpaceType(s.getSpaceType());
        vo.setSpaceTypeName("CHARGE".equals(s.getSpaceType()) ? "充电桩" : "普通");
        vo.setLocation(s.getLocation());
        vo.setStatus(s.getStatus());
        vo.setStatusName(s.getStatus() == 0 ? "空闲" : s.getStatus() == 1 ? "已占用" : "已锁定");
        vo.setOwnerId(s.getOwnerId());
        vo.setRemark(s.getRemark());
        vo.setCreateTime(s.getCreateTime());
        if (s.getOwnerId() != null) {
            OwnerInfo ownerInfo = ownerInfoMapper.selectById(s.getOwnerId());
            if (ownerInfo != null) {
                SysUser user = sysUserMapper.selectById(ownerInfo.getUserId());
                vo.setOwnerName(user != null ? user.getRealName() : "");
            }
        }
        return vo;
    }

    private ParkingRecordVO toRecordVO(ParkingRecord r) {
        ParkingRecordVO vo = new ParkingRecordVO();
        vo.setId(r.getId());
        vo.setSpaceId(r.getSpaceId());
        vo.setCarNo(r.getCarNo());
        vo.setOwnerId(r.getOwnerId());
        vo.setStartTime(r.getStartTime());
        vo.setEndTime(r.getEndTime());
        vo.setFeeAmount(r.getFeeAmount());
        vo.setPayStatus(r.getPayStatus());
        vo.setPayStatusName(r.getPayStatus() == 1 ? "已缴费" : "未缴费");
        vo.setCreateTime(r.getCreateTime());
        ParkingSpace space = getById(r.getSpaceId());
        if (space != null) { vo.setSpaceNo(space.getSpaceNo()); vo.setSpaceLocation(space.getLocation()); }
        if (r.getOwnerId() != null) {
            OwnerInfo ownerInfo = ownerInfoMapper.selectById(r.getOwnerId());
            if (ownerInfo != null) {
                SysUser user = sysUserMapper.selectById(ownerInfo.getUserId());
                vo.setOwnerName(user != null ? user.getRealName() : "");
            }
        }
        return vo;
    }

    private void copySpaceFrom(ParkingSpaceAddDTO dto, ParkingSpace space) {
        space.setSpaceNo(dto.getSpaceNo());
        space.setSpaceType(StringUtils.hasText(dto.getSpaceType()) ? dto.getSpaceType() : "NORMAL");
        space.setLocation(dto.getLocation());
        space.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        space.setOwnerId(dto.getOwnerId());
        space.setRemark(dto.getRemark());
    }
}
