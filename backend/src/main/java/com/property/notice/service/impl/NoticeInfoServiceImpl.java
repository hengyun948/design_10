package com.property.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.exception.BusinessException;
import com.property.notice.dto.NoticeAddDTO;
import com.property.notice.dto.NoticeQueryDTO;
import com.property.notice.entity.NoticeInfo;
import com.property.notice.mapper.NoticeInfoMapper;
import com.property.notice.service.NoticeInfoService;
import com.property.notice.vo.NoticeVO;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NoticeInfoServiceImpl extends ServiceImpl<NoticeInfoMapper, NoticeInfo> implements NoticeInfoService {

    private final SysUserMapper sysUserMapper;

    private static final Map<String, String> TYPE_NAMES = Map.of(
            "NOTICE", "通知", "ACTIVITY", "活动", "URGENCY", "紧急"
    );

    @Override
    public PageResult<NoticeVO> page(NoticeQueryDTO dto) {
        LambdaQueryWrapper<NoticeInfo> wrapper = new LambdaQueryWrapper<NoticeInfo>()
                .eq(StringUtils.hasText(dto.getNoticeType()), NoticeInfo::getNoticeType, dto.getNoticeType())
                .eq(dto.getPublishStatus() != null, NoticeInfo::getPublishStatus, dto.getPublishStatus())
                .and(StringUtils.hasText(dto.getKeyword()), w ->
                        w.like(NoticeInfo::getTitle, dto.getKeyword())
                         .or().like(NoticeInfo::getContent, dto.getKeyword()))
                .orderByDesc(NoticeInfo::getCreateTime);
        Page<NoticeInfo> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public PageResult<NoticeVO> pagePublished(NoticeQueryDTO dto) {
        LambdaQueryWrapper<NoticeInfo> wrapper = new LambdaQueryWrapper<NoticeInfo>()
                .eq(NoticeInfo::getPublishStatus, 1)
                .eq(StringUtils.hasText(dto.getNoticeType()), NoticeInfo::getNoticeType, dto.getNoticeType())
                .and(StringUtils.hasText(dto.getKeyword()), w ->
                        w.like(NoticeInfo::getTitle, dto.getKeyword())
                         .or().like(NoticeInfo::getContent, dto.getKeyword()))
                .orderByDesc(NoticeInfo::getPublishTime);
        Page<NoticeInfo> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public NoticeVO getDetail(Long id) {
        NoticeInfo notice = getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        return toVO(notice);
    }

    @Override
    public Long add(NoticeAddDTO dto, Long publisherId) {
        NoticeInfo notice = new NoticeInfo();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setNoticeType(dto.getNoticeType());
        notice.setPublishStatus(0);
        notice.setPublisherId(publisherId);
        save(notice);
        return notice.getId();
    }

    @Override
    public void update(Long id, NoticeAddDTO dto) {
        NoticeInfo notice = getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        if (notice.getPublishStatus() == 1) throw new BusinessException("已发布的公告不可编辑，请先撤回");
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setNoticeType(dto.getNoticeType());
        updateById(notice);
    }

    @Override
    public void publish(Long id, Long publisherId) {
        NoticeInfo notice = getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        notice.setPublishStatus(1);
        notice.setPublisherId(publisherId);
        notice.setPublishTime(LocalDateTime.now());
        updateById(notice);
    }

    @Override
    public void unpublish(Long id) {
        NoticeInfo notice = getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        notice.setPublishStatus(0);
        updateById(notice);
    }

    @Override
    public void delete(Long id) {
        NoticeInfo notice = getById(id);
        if (notice == null) throw new BusinessException("公告不存在");
        removeById(id);
    }

    private NoticeVO toVO(NoticeInfo notice) {
        NoticeVO vo = new NoticeVO();
        vo.setId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setNoticeType(notice.getNoticeType());
        vo.setNoticeTypeName(notice.getNoticeType() != null
                ? TYPE_NAMES.getOrDefault(notice.getNoticeType(), notice.getNoticeType()) : "");
        vo.setPublishStatus(notice.getPublishStatus());
        vo.setPublishStatusName(notice.getPublishStatus() == 1 ? "已发布" : "草稿");
        vo.setPublisherId(notice.getPublisherId());
        if (notice.getPublisherId() != null) {
            SysUser user = sysUserMapper.selectById(notice.getPublisherId());
            vo.setPublisherName(user != null ? user.getRealName() : "");
        }
        vo.setPublishTime(notice.getPublishTime());
        vo.setCreateTime(notice.getCreateTime());
        return vo;
    }
}
