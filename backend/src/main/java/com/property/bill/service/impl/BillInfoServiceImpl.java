package com.property.bill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.bill.dto.BillAddDTO;
import com.property.bill.dto.BillQueryDTO;
import com.property.bill.entity.BillInfo;
import com.property.bill.entity.PaymentRecord;
import com.property.bill.mapper.BillInfoMapper;
import com.property.bill.mapper.PaymentRecordMapper;
import com.property.bill.service.BillInfoService;
import com.property.bill.vo.BillVO;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.house.entity.HouseInfo;
import com.property.house.mapper.HouseInfoMapper;
import com.property.owner.entity.OwnerInfo;
import com.property.owner.mapper.OwnerInfoMapper;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillInfoServiceImpl extends ServiceImpl<BillInfoMapper, BillInfo> implements BillInfoService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final HouseInfoMapper houseInfoMapper;
    private final OwnerInfoMapper ownerInfoMapper;
    private final SysUserMapper sysUserMapper;

    private static final Map<String, String> BILL_TYPE_NAMES = Map.of(
            "PROPERTY", "物业费", "WATER", "水费", "ELECTRIC", "电费",
            "GAS", "燃气费", "PARKING", "停车费"
    );
    private static final String[] STATUS_NAMES = {"未缴", "已缴", "逾期"};

    @Override
    public PageResult<BillVO> page(BillQueryDTO dto) {
        LambdaQueryWrapper<BillInfo> wrapper = buildWrapper(dto);
        Page<BillInfo> result = baseMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(result.convert(this::toVO));
    }

    @Override
    public PageResult<BillVO> myBills(BillQueryDTO dto, Long userId) {
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, userId));
        if (ownerInfo == null) return PageResult.of(new Page<>());
        dto.setOwnerId(ownerInfo.getId());
        return page(dto);
    }

    @Override
    public BillVO getDetail(Long id) {
        BillInfo bill = getById(id);
        if (bill == null) throw new BusinessException("账单不存在");
        return toVO(bill);
    }

    @Override
    public void add(BillAddDTO dto) {
        long dup = count(new LambdaQueryWrapper<BillInfo>()
                .eq(BillInfo::getHouseId, dto.getHouseId())
                .eq(BillInfo::getBillType, dto.getBillType())
                .eq(BillInfo::getBillingPeriod, dto.getBillingPeriod()));
        if (dup > 0) throw new BusinessException("该房屋此账期已存在相同类型账单");
        BillInfo bill = new BillInfo();
        bill.setBillNo(generateBillNo());
        bill.setHouseId(dto.getHouseId());
        bill.setOwnerId(dto.getOwnerId());
        bill.setBillType(dto.getBillType());
        bill.setBillingPeriod(dto.getBillingPeriod());
        bill.setAmountDue(dto.getAmountDue());
        bill.setAmountPaid(BigDecimal.ZERO);
        bill.setStatus(0);
        bill.setDueDate(dto.getDueDate());
        bill.setRemark(dto.getRemark());
        save(bill);
    }

    @Override
    @Transactional
    public void pay(Long billId, Long operatorId) {
        BillInfo bill = getById(billId);
        if (bill == null) throw new BusinessException("账单不存在");
        if (bill.getStatus() == 1) throw new BusinessException("账单已缴费");
        bill.setAmountPaid(bill.getAmountDue());
        bill.setStatus(1);
        updateById(bill);
        PaymentRecord record = new PaymentRecord();
        record.setBillId(billId);
        record.setPayNo(generatePayNo());
        record.setPayAmount(bill.getAmountDue());
        record.setPayType("MOCK");
        record.setPayStatus(1);
        record.setPayTime(LocalDateTime.now());
        record.setOperatorId(operatorId);
        paymentRecordMapper.insert(record);
    }

    @Override
    public List<Object> getPaymentRecords(Long billId) {
        return paymentRecordMapper.selectList(
                new LambdaQueryWrapper<PaymentRecord>().eq(PaymentRecord::getBillId, billId)
                        .orderByDesc(PaymentRecord::getPayTime))
                .stream().map(r -> (Object) r).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        BillInfo bill = getById(id);
        if (bill == null) throw new BusinessException("账单不存在");
        if (bill.getStatus() == 1) throw new BusinessException("已缴费账单不可删除");
        removeById(id);
    }

    private LambdaQueryWrapper<BillInfo> buildWrapper(BillQueryDTO dto) {
        return new LambdaQueryWrapper<BillInfo>()
                .eq(dto.getHouseId() != null, BillInfo::getHouseId, dto.getHouseId())
                .eq(dto.getOwnerId() != null, BillInfo::getOwnerId, dto.getOwnerId())
                .eq(StringUtils.hasText(dto.getBillType()), BillInfo::getBillType, dto.getBillType())
                .eq(StringUtils.hasText(dto.getBillingPeriod()), BillInfo::getBillingPeriod, dto.getBillingPeriod())
                .eq(dto.getStatus() != null, BillInfo::getStatus, dto.getStatus())
                .orderByDesc(BillInfo::getCreateTime);
    }

    private BillVO toVO(BillInfo bill) {
        BillVO vo = new BillVO();
        vo.setId(bill.getId());
        vo.setBillNo(bill.getBillNo());
        vo.setHouseId(bill.getHouseId());
        vo.setOwnerId(bill.getOwnerId());
        vo.setBillType(bill.getBillType());
        vo.setBillTypeName(BILL_TYPE_NAMES.getOrDefault(bill.getBillType(), bill.getBillType()));
        vo.setBillingPeriod(bill.getBillingPeriod());
        vo.setAmountDue(bill.getAmountDue());
        vo.setAmountPaid(bill.getAmountPaid());
        vo.setStatus(bill.getStatus());
        vo.setStatusName(bill.getStatus() != null && bill.getStatus() < STATUS_NAMES.length
                ? STATUS_NAMES[bill.getStatus()] : "");
        vo.setDueDate(bill.getDueDate());
        vo.setRemark(bill.getRemark());
        vo.setCreateTime(bill.getCreateTime());
        // 补充房屋地址
        if (bill.getHouseId() != null) {
            HouseInfo house = houseInfoMapper.selectById(bill.getHouseId());
            if (house != null) {
                vo.setHouseAddress(house.getBuildingNo() + "栋" + house.getUnitNo() + "单元" + house.getRoomNo() + "室");
            }
        }
        // 补充业主信息
        if (bill.getOwnerId() != null) {
            OwnerInfo ownerInfo = ownerInfoMapper.selectById(bill.getOwnerId());
            if (ownerInfo != null) {
                SysUser user = sysUserMapper.selectById(ownerInfo.getUserId());
                if (user != null) {
                    vo.setOwnerName(user.getRealName());
                    vo.setOwnerPhone(user.getPhone());
                }
            }
        }
        return vo;
    }

    private static final AtomicLong billSeq = new AtomicLong(System.currentTimeMillis() % 100000);

    private String generateBillNo() {
        return "BILL" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now())
                + String.format("%05d", billSeq.incrementAndGet() % 100000);
    }

    private String generatePayNo() {
        return "PAY" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())
                + String.format("%04d", (int) (Math.random() * 10000));
    }
}
