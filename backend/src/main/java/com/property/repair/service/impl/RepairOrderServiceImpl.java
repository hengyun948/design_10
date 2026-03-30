package com.property.repair.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.house.entity.HouseInfo;
import com.property.house.mapper.HouseInfoMapper;
import com.property.owner.entity.OwnerInfo;
import com.property.owner.mapper.OwnerInfoMapper;
import com.property.repair.dto.RepairAssignDTO;
import com.property.repair.dto.RepairOrderAddDTO;
import com.property.repair.dto.RepairOrderQueryDTO;
import com.property.repair.entity.RepairOrder;
import com.property.repair.entity.RepairProcessRecord;
import com.property.repair.mapper.RepairOrderMapper;
import com.property.repair.mapper.RepairProcessRecordMapper;
import com.property.repair.service.RepairOrderService;
import com.property.repair.vo.RepairOrderVO;
import com.property.repair.vo.RepairProcessVO;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {

    private final RepairProcessRecordMapper processRecordMapper;
    private final SysUserMapper sysUserMapper;
    private final OwnerInfoMapper ownerInfoMapper;
    private final HouseInfoMapper houseInfoMapper;

    private static final String[] STATUS_NAMES = {"待受理", "已受理", "已分派", "处理中", "已完成", "已取消"};
    private static final String[] PRIORITY_NAMES = {"", "紧急", "普通", "低"};
    private static final Map<String, String> TYPE_NAMES = Map.of(
            "WATER", "水管", "ELECTRIC", "电路", "DOOR", "门窗", "OTHER", "其他"
    );
    private static final Map<String, String> ACTION_NAMES = Map.of(
            "SUBMIT", "提交", "ACCEPT", "受理", "ASSIGN", "派单",
            "RECEIVE", "接单", "UPDATE", "进度更新", "FINISH", "完成", "CANCEL", "取消"
    );

    @Override
    public PageResult<RepairOrderVO> page(RepairOrderQueryDTO dto) {
        LambdaQueryWrapper<RepairOrder> wrapper = buildWrapper(dto);
        Page<RepairOrder> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public PageResult<RepairOrderVO> myOrders(RepairOrderQueryDTO dto) {
        // dto.ownerId 传入的是 sys_user.id，需转换为 owner_info.id
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, dto.getOwnerId()));
        if (ownerInfo == null) {
            Page<RepairOrder> empty = new Page<>(dto.getPageNum(), dto.getPageSize());
            empty.setTotal(0);
            return PageResult.of(empty.convert(this::toVO));
        }
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<RepairOrder>()
                .eq(RepairOrder::getOwnerId, ownerInfo.getId())
                .eq(dto.getStatus() != null, RepairOrder::getStatus, dto.getStatus())
                .orderByDesc(RepairOrder::getCreateTime);
        Page<RepairOrder> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public PageResult<RepairOrderVO> assignedOrders(RepairOrderQueryDTO dto) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<RepairOrder>()
                .eq(RepairOrder::getAssignedWorkerId, dto.getAssignedWorkerId())
                .eq(dto.getStatus() != null, RepairOrder::getStatus, dto.getStatus())
                .orderByDesc(RepairOrder::getCreateTime);
        Page<RepairOrder> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public RepairOrderVO getDetail(Long id) {
        RepairOrder order = getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        return toVO(order);
    }

    @Override
    public List<RepairProcessVO> getRecords(Long id) {
        List<RepairProcessRecord> records = processRecordMapper.selectList(
                new LambdaQueryWrapper<RepairProcessRecord>()
                        .eq(RepairProcessRecord::getRepairOrderId, id)
                        .orderByAsc(RepairProcessRecord::getRecordTime));
        return records.stream().map(r -> {
            RepairProcessVO vo = new RepairProcessVO();
            vo.setId(r.getId());
            vo.setActionType(r.getActionType());
            vo.setActionTypeName(ACTION_NAMES.getOrDefault(r.getActionType(), r.getActionType()));
            vo.setActionContent(r.getActionContent());
            vo.setProcessStatus(r.getProcessStatus());
            vo.setStatusName(r.getProcessStatus() != null && r.getProcessStatus() < STATUS_NAMES.length
                    ? STATUS_NAMES[r.getProcessStatus()] : "");
            vo.setOperatorId(r.getOperatorId());
            SysUser op = sysUserMapper.selectById(r.getOperatorId());
            vo.setOperatorName(op != null ? op.getRealName() : "");
            vo.setRecordTime(r.getRecordTime());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long submit(RepairOrderAddDTO dto, Long ownerId) {
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, ownerId));
        if (ownerInfo == null) throw new BusinessException("业主信息不存在");
        RepairOrder order = new RepairOrder();
        order.setOrderNo(generateOrderNo());
        order.setOwnerId(ownerInfo.getId());
        order.setHouseId(dto.getHouseId());
        order.setTitle(dto.getTitle());
        order.setContent(dto.getContent());
        order.setRepairType(dto.getRepairType());
        order.setPriority(dto.getPriority() != null ? dto.getPriority() : 2);
        order.setStatus(0);
        order.setImageUrls(dto.getImageUrls());
        save(order);
        addRecord(order.getId(), ownerId, "SUBMIT", "提交报修工单：" + dto.getTitle(), 0);
        return order.getId();
    }

    @Override
    @Transactional
    public void accept(Long id, Long operatorId) {
        RepairOrder order = getAndCheck(id, 0, "工单当前状态不可受理");
        order.setStatus(1);
        order.setAcceptTime(LocalDateTime.now());
        updateById(order);
        addRecord(id, operatorId, "ACCEPT", "已受理工单", 1);
    }

    @Override
    @Transactional
    public void assign(Long id, RepairAssignDTO dto, Long operatorId) {
        RepairOrder order = getAndCheck(id, 1, "工单须先受理才能派单");
        order.setStatus(2);
        order.setAssignedWorkerId(dto.getWorkerId());
        order.setAssignTime(LocalDateTime.now());
        updateById(order);
        SysUser worker = sysUserMapper.selectById(dto.getWorkerId());
        String workerName = worker != null ? worker.getRealName() : "";
        String content = "已派单给 " + workerName + (StringUtils.hasText(dto.getRemark()) ? "，备注：" + dto.getRemark() : "");
        addRecord(id, operatorId, "ASSIGN", content, 2);
    }

    @Override
    @Transactional
    public void receive(Long id, Long workerId) {
        RepairOrder order = getAndCheck(id, 2, "工单未分派给您或状态不正确");
        if (!workerId.equals(order.getAssignedWorkerId())) throw new BusinessException("该工单未分派给您");
        order.setStatus(3);
        updateById(order);
        addRecord(id, workerId, "RECEIVE", "维修人员已接单，开始处理", 3);
    }

    @Override
    @Transactional
    public void finish(Long id, Long workerId, String remark) {
        RepairOrder order = getAndCheck(id, 3, "工单状态不正确");
        if (!workerId.equals(order.getAssignedWorkerId())) throw new BusinessException("该工单未分派给您");
        order.setStatus(4);
        order.setFinishTime(LocalDateTime.now());
        updateById(order);
        String content = "维修完成" + (StringUtils.hasText(remark) ? "，说明：" + remark : "");
        addRecord(id, workerId, "FINISH", content, 4);
    }

    @Override
    @Transactional
    public void cancel(Long id, Long operatorId, String reason) {
        RepairOrder order = getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() >= 4) throw new BusinessException("已完成的工单不能取消");
        order.setStatus(5);
        updateById(order);
        addRecord(id, operatorId, "CANCEL", StringUtils.hasText(reason) ? "取消原因：" + reason : "已取消", 5);
    }

    private LambdaQueryWrapper<RepairOrder> buildWrapper(RepairOrderQueryDTO dto) {
        return new LambdaQueryWrapper<RepairOrder>()
                .eq(dto.getStatus() != null, RepairOrder::getStatus, dto.getStatus())
                .eq(StringUtils.hasText(dto.getRepairType()), RepairOrder::getRepairType, dto.getRepairType())
                .and(StringUtils.hasText(dto.getKeyword()), w -> w
                        .like(RepairOrder::getTitle, dto.getKeyword())
                        .or().like(RepairOrder::getOrderNo, dto.getKeyword()))
                .orderByDesc(RepairOrder::getCreateTime);
    }

    private RepairOrder getAndCheck(Long id, int expectedStatus, String msg) {
        RepairOrder order = getById(id);
        if (order == null) throw new BusinessException("工单不存在");
        if (order.getStatus() != expectedStatus) throw new BusinessException(msg);
        return order;
    }

    private void addRecord(Long orderId, Long operatorId, String actionType, String content, int status) {
        RepairProcessRecord record = new RepairProcessRecord();
        record.setRepairOrderId(orderId);
        record.setOperatorId(operatorId);
        record.setActionType(actionType);
        record.setActionContent(content);
        record.setProcessStatus(status);
        record.setRecordTime(LocalDateTime.now());
        processRecordMapper.insert(record);
    }

    private String generateOrderNo() {
        return "WX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", (int) (Math.random() * 1000));
    }

    @Override
    public Map<String, Long> workerStats(Long workerId) {
        return Map.of(
                "pending", count(new LambdaQueryWrapper<RepairOrder>()
                        .eq(RepairOrder::getAssignedWorkerId, workerId)
                        .eq(RepairOrder::getStatus, 2)),
                "processing", count(new LambdaQueryWrapper<RepairOrder>()
                        .eq(RepairOrder::getAssignedWorkerId, workerId)
                        .eq(RepairOrder::getStatus, 3)),
                "done", count(new LambdaQueryWrapper<RepairOrder>()
                        .eq(RepairOrder::getAssignedWorkerId, workerId)
                        .eq(RepairOrder::getStatus, 4))
        );
    }

    private RepairOrderVO toVO(RepairOrder order) {
        RepairOrderVO vo = new RepairOrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setOwnerId(order.getOwnerId());
        vo.setHouseId(order.getHouseId());
        vo.setTitle(order.getTitle());
        vo.setContent(order.getContent());
        vo.setRepairType(order.getRepairType());
        vo.setRepairTypeName(order.getRepairType() != null ? TYPE_NAMES.getOrDefault(order.getRepairType(), order.getRepairType()) : "");
        vo.setPriority(order.getPriority());
        vo.setPriorityName(order.getPriority() != null && order.getPriority() < PRIORITY_NAMES.length ? PRIORITY_NAMES[order.getPriority()] : "");
        vo.setStatus(order.getStatus());
        vo.setStatusName(order.getStatus() != null && order.getStatus() < STATUS_NAMES.length ? STATUS_NAMES[order.getStatus()] : "");
        vo.setAssignedWorkerId(order.getAssignedWorkerId());
        vo.setAcceptTime(order.getAcceptTime());
        vo.setAssignTime(order.getAssignTime());
        vo.setFinishTime(order.getFinishTime());
        vo.setImageUrls(order.getImageUrls());
        vo.setCreateTime(order.getCreateTime());
        // 业主信息
        OwnerInfo ownerInfo = ownerInfoMapper.selectById(order.getOwnerId());
        if (ownerInfo != null) {
            SysUser ownerUser = sysUserMapper.selectById(ownerInfo.getUserId());
            if (ownerUser != null) {
                vo.setOwnerName(ownerUser.getRealName());
                vo.setOwnerPhone(ownerUser.getPhone());
            }
        }
        // 房屋信息
        HouseInfo house = houseInfoMapper.selectById(order.getHouseId());
        if (house != null) {
            vo.setHouseAddress(house.getBuildingNo() + "栋" + house.getUnitNo() + "单元" + house.getRoomNo() + "室");
        }
        // 维修人员信息
        if (order.getAssignedWorkerId() != null) {
            SysUser worker = sysUserMapper.selectById(order.getAssignedWorkerId());
            if (worker != null) {
                vo.setWorkerName(worker.getRealName());
                vo.setWorkerPhone(worker.getPhone());
            }
        }
        return vo;
    }
}
