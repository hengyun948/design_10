package com.property.activity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.activity.dto.ActivityAddDTO;
import com.property.activity.dto.ActivityQueryDTO;
import com.property.activity.entity.ActivityInfo;
import com.property.activity.entity.ActivitySignup;
import com.property.activity.mapper.ActivityInfoMapper;
import com.property.activity.mapper.ActivitySignupMapper;
import com.property.activity.service.ActivityService;
import com.property.activity.vo.ActivitySignupVO;
import com.property.activity.vo.ActivityVO;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.owner.entity.OwnerInfo;
import com.property.owner.mapper.OwnerInfoMapper;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo> implements ActivityService {

    private final ActivitySignupMapper signupMapper;
    private final SysUserMapper sysUserMapper;
    private final OwnerInfoMapper ownerInfoMapper;

    private static final Map<String, String> TYPE_NAMES = Map.of("ACTIVITY", "社区活动", "CARE", "关怀服务");
    private static final String[] STATUS_NAMES = {"已取消", "报名中", "已结束"};

    @Override
    public PageResult<ActivityVO> page(ActivityQueryDTO dto) {
        Page<ActivityInfo> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), buildWrapper(dto));
        return PageResult.of(page.convert(a -> toVO(a, null)));
    }

    @Override
    public PageResult<ActivityVO> pageForOwner(ActivityQueryDTO dto, Long ownerUserId) {
        LambdaQueryWrapper<ActivityInfo> wrapper = buildWrapper(dto)
                .in(ActivityInfo::getStatus, 1, 2);
        Page<ActivityInfo> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, ownerUserId));
        Long ownerId = ownerInfo != null ? ownerInfo.getId() : null;
        return PageResult.of(page.convert(a -> toVO(a, ownerId)));
    }

    @Override
    public ActivityVO getDetail(Long id, Long ownerUserId) {
        ActivityInfo a = getById(id);
        if (a == null) throw new BusinessException("活动不存在");
        OwnerInfo ownerInfo = ownerUserId == null ? null :
                ownerInfoMapper.selectOne(new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, ownerUserId));
        return toVO(a, ownerInfo != null ? ownerInfo.getId() : null);
    }

    @Override
    public Long add(ActivityAddDTO dto, Long publisherId) {
        ActivityInfo a = new ActivityInfo();
        copyFrom(dto, a);
        a.setStatus(1);
        a.setPublisherId(publisherId);
        save(a);
        return a.getId();
    }

    @Override
    public void update(Long id, ActivityAddDTO dto) {
        ActivityInfo a = getById(id);
        if (a == null) throw new BusinessException("活动不存在");
        if (a.getStatus() != 1) throw new BusinessException("仅报名中的活动可编辑");
        copyFrom(dto, a);
        updateById(a);
    }

    @Override
    public void cancel(Long id) {
        ActivityInfo a = getById(id);
        if (a == null) throw new BusinessException("活动不存在");
        a.setStatus(0);
        updateById(a);
    }

    @Override
    public void finish(Long id) {
        ActivityInfo a = getById(id);
        if (a == null) throw new BusinessException("活动不存在");
        a.setStatus(2);
        updateById(a);
    }

    @Override
    public void signup(Long activityId, Long ownerUserId) {
        ActivityInfo a = getById(activityId);
        if (a == null || a.getStatus() != 1) throw new BusinessException("活动不在报名中");
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, ownerUserId));
        if (ownerInfo == null) throw new BusinessException("业主信息不存在");
        // 检查是否已报名
        Long existing = signupMapper.selectCount(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getOwnerId, ownerInfo.getId())
                .eq(ActivitySignup::getStatus, 1));
        if (existing > 0) throw new BusinessException("您已报名该活动");
        // 检查人数上限
        if (a.getMaxParticipants() != null) {
            long count = signupMapper.selectCount(new LambdaQueryWrapper<ActivitySignup>()
                    .eq(ActivitySignup::getActivityId, activityId)
                    .eq(ActivitySignup::getStatus, 1));
            if (count >= a.getMaxParticipants()) throw new BusinessException("活动名额已满");
        }
        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setOwnerId(ownerInfo.getId());
        signup.setSignupTime(LocalDateTime.now());
        signup.setStatus(1);
        signupMapper.insert(signup);
    }

    @Override
    public void cancelSignup(Long activityId, Long ownerUserId) {
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, ownerUserId));
        if (ownerInfo == null) throw new BusinessException("业主信息不存在");
        ActivitySignup signup = signupMapper.selectOne(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getOwnerId, ownerInfo.getId())
                .eq(ActivitySignup::getStatus, 1));
        if (signup == null) throw new BusinessException("未找到报名记录");
        signup.setStatus(0);
        signupMapper.updateById(signup);
    }

    @Override
    public List<ActivitySignupVO> listSignups(Long activityId) {
        List<ActivitySignup> list = signupMapper.selectList(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getStatus, 1)
                .orderByAsc(ActivitySignup::getSignupTime));
        ActivityInfo activity = getById(activityId);
        return list.stream().map(s -> {
            ActivitySignupVO vo = new ActivitySignupVO();
            vo.setId(s.getId());
            vo.setActivityId(activityId);
            vo.setActivityTitle(activity != null ? activity.getTitle() : "");
            vo.setOwnerId(s.getOwnerId());
            vo.setSignupTime(s.getSignupTime());
            vo.setStatus(s.getStatus());
            vo.setStatusName(s.getStatus() == 1 ? "已报名" : "已取消");
            vo.setRemark(s.getRemark());
            OwnerInfo ownerInfo = ownerInfoMapper.selectById(s.getOwnerId());
            if (ownerInfo != null) {
                SysUser user = sysUserMapper.selectById(ownerInfo.getUserId());
                if (user != null) { vo.setOwnerName(user.getRealName()); vo.setOwnerPhone(user.getPhone()); }
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<ActivityInfo> buildWrapper(ActivityQueryDTO dto) {
        return new LambdaQueryWrapper<ActivityInfo>()
                .eq(StringUtils.hasText(dto.getActivityType()), ActivityInfo::getActivityType, dto.getActivityType())
                .eq(dto.getStatus() != null, ActivityInfo::getStatus, dto.getStatus())
                .and(StringUtils.hasText(dto.getKeyword()), w ->
                        w.like(ActivityInfo::getTitle, dto.getKeyword())
                         .or().like(ActivityInfo::getLocation, dto.getKeyword()))
                .orderByDesc(ActivityInfo::getActivityTime);
    }

    private ActivityVO toVO(ActivityInfo a, Long ownerId) {
        ActivityVO vo = new ActivityVO();
        vo.setId(a.getId());
        vo.setTitle(a.getTitle());
        vo.setContent(a.getContent());
        vo.setActivityType(a.getActivityType());
        vo.setActivityTypeName(TYPE_NAMES.getOrDefault(a.getActivityType(), a.getActivityType() != null ? a.getActivityType() : ""));
        vo.setActivityTime(a.getActivityTime());
        vo.setLocation(a.getLocation());
        vo.setMaxParticipants(a.getMaxParticipants());
        vo.setStatus(a.getStatus());
        vo.setStatusName(a.getStatus() != null && a.getStatus() < STATUS_NAMES.length ? STATUS_NAMES[a.getStatus()] : "");
        vo.setPublisherId(a.getPublisherId());
        vo.setCreateTime(a.getCreateTime());
        long signupCount = signupMapper.selectCount(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getActivityId, a.getId()).eq(ActivitySignup::getStatus, 1));
        vo.setSignupCount((int) signupCount);
        if (a.getPublisherId() != null) {
            SysUser user = sysUserMapper.selectById(a.getPublisherId());
            vo.setPublisherName(user != null ? user.getRealName() : "");
        }
        if (ownerId != null) {
            long signed = signupMapper.selectCount(new LambdaQueryWrapper<ActivitySignup>()
                    .eq(ActivitySignup::getActivityId, a.getId())
                    .eq(ActivitySignup::getOwnerId, ownerId)
                    .eq(ActivitySignup::getStatus, 1));
            vo.setSignedUp(signed > 0);
        }
        return vo;
    }

    private void copyFrom(ActivityAddDTO dto, ActivityInfo a) {
        a.setTitle(dto.getTitle());
        a.setContent(dto.getContent());
        a.setActivityType(dto.getActivityType());
        a.setActivityTime(dto.getActivityTime());
        a.setLocation(dto.getLocation());
        a.setMaxParticipants(dto.getMaxParticipants());
    }
}
