package com.property.log.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.property.activity.entity.ActivityInfo;
import com.property.activity.entity.ActivitySignup;
import com.property.activity.mapper.ActivityInfoMapper;
import com.property.activity.mapper.ActivitySignupMapper;
import com.property.bill.entity.BillInfo;
import com.property.bill.mapper.BillInfoMapper;
import com.property.common.api.Result;
import com.property.complaint.entity.ComplaintSuggestion;
import com.property.complaint.mapper.ComplaintSuggestionMapper;
import com.property.house.entity.HouseInfo;
import com.property.house.mapper.HouseInfoMapper;
import com.property.notice.entity.NoticeInfo;
import com.property.notice.mapper.NoticeInfoMapper;
import com.property.owner.entity.OwnerInfo;
import com.property.owner.mapper.OwnerInfoMapper;
import com.property.parking.entity.ParkingRecord;
import com.property.parking.entity.ParkingSpace;
import com.property.parking.mapper.ParkingRecordMapper;
import com.property.parking.mapper.ParkingSpaceMapper;
import com.property.repair.entity.RepairOrder;
import com.property.repair.mapper.RepairOrderMapper;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final SysUserMapper sysUserMapper;
    private final OwnerInfoMapper ownerInfoMapper;
    private final HouseInfoMapper houseInfoMapper;
    private final RepairOrderMapper repairOrderMapper;
    private final BillInfoMapper billInfoMapper;
    private final NoticeInfoMapper noticeInfoMapper;
    private final ComplaintSuggestionMapper complaintMapper;
    private final ActivityInfoMapper activityInfoMapper;
    private final ActivitySignupMapper activitySignupMapper;
    private final ParkingSpaceMapper parkingSpaceMapper;
    private final ParkingRecordMapper parkingRecordMapper;

    @GetMapping("/overview")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<OverviewVO> overview() {
        OverviewVO vo = new OverviewVO();
        vo.setUserCount(sysUserMapper.selectCount(null));
        vo.setOwnerCount(ownerInfoMapper.selectCount(null));
        vo.setHouseCount(houseInfoMapper.selectCount(null));
        vo.setPendingRepair(repairOrderMapper.selectCount(
                new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, 0)));
        vo.setTotalRepair(repairOrderMapper.selectCount(null));
        vo.setPendingBill(billInfoMapper.selectCount(
                new LambdaQueryWrapper<BillInfo>().eq(BillInfo::getStatus, 0)));
        vo.setPublishedNotice(noticeInfoMapper.selectCount(
                new LambdaQueryWrapper<NoticeInfo>().eq(NoticeInfo::getPublishStatus, 1)));
        vo.setPendingComplaint(complaintMapper.selectCount(
                new LambdaQueryWrapper<ComplaintSuggestion>().eq(ComplaintSuggestion::getStatus, 0)));
        vo.setActiveActivity(activityInfoMapper.selectCount(
                new LambdaQueryWrapper<ActivityInfo>().eq(ActivityInfo::getStatus, 1)));
        vo.setFreeSpace(parkingSpaceMapper.selectCount(
                new LambdaQueryWrapper<ParkingSpace>().eq(ParkingSpace::getStatus, 0)));
        vo.setTotalSpace(parkingSpaceMapper.selectCount(null));
        return Result.success(vo);
    }

    @GetMapping("/repair-status")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Map<String, Long>> repairStatus() {
        Map<String, Long> map = new HashMap<>();
        map.put("待受理", repairOrderMapper.selectCount(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, 0)));
        map.put("已受理", repairOrderMapper.selectCount(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, 1)));
        map.put("已分派", repairOrderMapper.selectCount(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, 2)));
        map.put("处理中", repairOrderMapper.selectCount(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, 3)));
        map.put("已完成", repairOrderMapper.selectCount(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, 4)));
        map.put("已取消", repairOrderMapper.selectCount(new LambdaQueryWrapper<RepairOrder>().eq(RepairOrder::getStatus, 5)));
        return Result.success(map);
    }

    @GetMapping("/bill-status")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Map<String, Object>> billStatus() {
        Map<String, Object> map = new HashMap<>();
        map.put("未缴", billInfoMapper.selectCount(new LambdaQueryWrapper<BillInfo>().eq(BillInfo::getStatus, 0)));
        map.put("已缴", billInfoMapper.selectCount(new LambdaQueryWrapper<BillInfo>().eq(BillInfo::getStatus, 1)));
        map.put("逾期", billInfoMapper.selectCount(new LambdaQueryWrapper<BillInfo>().eq(BillInfo::getStatus, 2)));
        BigDecimal totalDue = billInfoMapper.selectList(null).stream()
                .map(BillInfo::getAmountDue).filter(a -> a != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = billInfoMapper.selectList(null).stream()
                .map(BillInfo::getAmountPaid).filter(a -> a != null).reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("应缴总额", totalDue);
        map.put("已缴总额", totalPaid);
        return Result.success(map);
    }

    @GetMapping("/house-status")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Map<String, Long>> houseStatus() {
        Map<String, Long> map = new HashMap<>();
        map.put("待售", houseInfoMapper.selectCount(new LambdaQueryWrapper<HouseInfo>().eq(HouseInfo::getHouseStatus, 0)));
        map.put("已售", houseInfoMapper.selectCount(new LambdaQueryWrapper<HouseInfo>().eq(HouseInfo::getHouseStatus, 1)));
        map.put("已租", houseInfoMapper.selectCount(new LambdaQueryWrapper<HouseInfo>().eq(HouseInfo::getHouseStatus, 2)));
        return Result.success(map);
    }

    @GetMapping("/parking-status")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Map<String, Long>> parkingStatus() {
        Map<String, Long> map = new HashMap<>();
        map.put("空闲", parkingSpaceMapper.selectCount(new LambdaQueryWrapper<ParkingSpace>().eq(ParkingSpace::getStatus, 0)));
        map.put("已占用", parkingSpaceMapper.selectCount(new LambdaQueryWrapper<ParkingSpace>().eq(ParkingSpace::getStatus, 1)));
        map.put("已锁定", parkingSpaceMapper.selectCount(new LambdaQueryWrapper<ParkingSpace>().eq(ParkingSpace::getStatus, 2)));
        map.put("未缴费记录", parkingRecordMapper.selectCount(new LambdaQueryWrapper<ParkingRecord>().eq(ParkingRecord::getPayStatus, 0)));
        return Result.success(map);
    }

    @GetMapping("/complaint-category")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Map<String, Long>> complaintCategory() {
        Map<String, Long> map = new HashMap<>();
        map.put("投诉", complaintMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestion>().eq(ComplaintSuggestion::getCategory, "COMPLAINT")));
        map.put("建议", complaintMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestion>().eq(ComplaintSuggestion::getCategory, "SUGGESTION")));
        map.put("待处理", complaintMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestion>().eq(ComplaintSuggestion::getStatus, 0)));
        map.put("已处理", complaintMapper.selectCount(new LambdaQueryWrapper<ComplaintSuggestion>().eq(ComplaintSuggestion::getStatus, 2)));
        return Result.success(map);
    }

    @Data
    public static class OverviewVO {
        private Long userCount;
        private Long ownerCount;
        private Long houseCount;
        private Long pendingRepair;
        private Long totalRepair;
        private Long pendingBill;
        private Long publishedNotice;
        private Long pendingComplaint;
        private Long activeActivity;
        private Long freeSpace;
        private Long totalSpace;
    }
}
