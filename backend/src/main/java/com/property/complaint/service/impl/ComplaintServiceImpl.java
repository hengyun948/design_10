package com.property.complaint.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.property.common.model.PageResult;
import com.property.complaint.dto.ComplaintAddDTO;
import com.property.complaint.dto.ComplaintQueryDTO;
import com.property.complaint.entity.ComplaintSuggestion;
import com.property.complaint.mapper.ComplaintSuggestionMapper;
import com.property.complaint.service.ComplaintService;
import com.property.complaint.vo.ComplaintVO;
import com.property.exception.BusinessException;
import com.property.owner.entity.OwnerInfo;
import com.property.owner.mapper.OwnerInfoMapper;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl extends ServiceImpl<ComplaintSuggestionMapper, ComplaintSuggestion> implements ComplaintService {

    private final SysUserMapper sysUserMapper;
    private final OwnerInfoMapper ownerInfoMapper;

    private static final String[] STATUS_NAMES = {"待处理", "处理中", "已处理"};
    private static final Map<String, String> CATEGORY_NAMES = Map.of(
            "COMPLAINT", "投诉", "SUGGESTION", "建议"
    );

    @Override
    public PageResult<ComplaintVO> page(ComplaintQueryDTO dto) {
        LambdaQueryWrapper<ComplaintSuggestion> wrapper = new LambdaQueryWrapper<ComplaintSuggestion>()
                .eq(StringUtils.hasText(dto.getCategory()), ComplaintSuggestion::getCategory, dto.getCategory())
                .eq(dto.getStatus() != null, ComplaintSuggestion::getStatus, dto.getStatus())
                .and(StringUtils.hasText(dto.getKeyword()), w ->
                        w.like(ComplaintSuggestion::getTitle, dto.getKeyword())
                         .or().like(ComplaintSuggestion::getContent, dto.getKeyword()))
                .orderByDesc(ComplaintSuggestion::getCreateTime);
        Page<ComplaintSuggestion> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public PageResult<ComplaintVO> myList(ComplaintQueryDTO dto) {
        // dto.ownerId is sys_user.id — convert to owner_info.id
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, dto.getOwnerId()));
        if (ownerInfo == null) {
            Page<ComplaintSuggestion> empty = new Page<>(dto.getPageNum(), dto.getPageSize());
            empty.setTotal(0);
            return PageResult.of(empty.convert(this::toVO));
        }
        LambdaQueryWrapper<ComplaintSuggestion> wrapper = new LambdaQueryWrapper<ComplaintSuggestion>()
                .eq(ComplaintSuggestion::getOwnerId, ownerInfo.getId())
                .eq(StringUtils.hasText(dto.getCategory()), ComplaintSuggestion::getCategory, dto.getCategory())
                .orderByDesc(ComplaintSuggestion::getCreateTime);
        Page<ComplaintSuggestion> page = this.page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        return PageResult.of(page.convert(this::toVO));
    }

    @Override
    public ComplaintVO getDetail(Long id) {
        ComplaintSuggestion c = getById(id);
        if (c == null) throw new BusinessException("记录不存在");
        return toVO(c);
    }

    @Override
    public Long submit(ComplaintAddDTO dto, Long userId) {
        OwnerInfo ownerInfo = ownerInfoMapper.selectOne(
                new LambdaQueryWrapper<OwnerInfo>().eq(OwnerInfo::getUserId, userId));
        if (ownerInfo == null) throw new BusinessException("业主信息不存在");
        ComplaintSuggestion c = new ComplaintSuggestion();
        c.setOwnerId(ownerInfo.getId());
        c.setTitle(dto.getTitle());
        c.setContent(dto.getContent());
        c.setCategory(StringUtils.hasText(dto.getCategory()) ? dto.getCategory() : "COMPLAINT");
        c.setStatus(0);
        save(c);
        return c.getId();
    }

    @Override
    public void handle(Long id, Long operatorId) {
        ComplaintSuggestion c = getById(id);
        if (c == null) throw new BusinessException("记录不存在");
        if (c.getStatus() != 0) throw new BusinessException("当前状态无需重复受理");
        c.setStatus(1);
        updateById(c);
    }

    @Override
    public void reply(Long id, String replyContent, Long operatorId) {
        ComplaintSuggestion c = getById(id);
        if (c == null) throw new BusinessException("记录不存在");
        c.setStatus(2);
        c.setReplyContent(replyContent);
        c.setReplyBy(operatorId);
        c.setReplyTime(LocalDateTime.now());
        updateById(c);
    }

    @Override
    public void delete(Long id) {
        if (!removeById(id)) throw new BusinessException("删除失败");
    }

    private ComplaintVO toVO(ComplaintSuggestion c) {
        ComplaintVO vo = new ComplaintVO();
        vo.setId(c.getId());
        vo.setOwnerId(c.getOwnerId());
        vo.setTitle(c.getTitle());
        vo.setContent(c.getContent());
        vo.setCategory(c.getCategory());
        vo.setCategoryName(CATEGORY_NAMES.getOrDefault(c.getCategory(), c.getCategory()));
        vo.setStatus(c.getStatus());
        vo.setStatusName(c.getStatus() != null && c.getStatus() < STATUS_NAMES.length
                ? STATUS_NAMES[c.getStatus()] : "");
        vo.setReplyContent(c.getReplyContent());
        vo.setReplyBy(c.getReplyBy());
        vo.setReplyTime(c.getReplyTime());
        vo.setCreateTime(c.getCreateTime());
        // 业主信息
        OwnerInfo ownerInfo = ownerInfoMapper.selectById(c.getOwnerId());
        if (ownerInfo != null) {
            SysUser user = sysUserMapper.selectById(ownerInfo.getUserId());
            if (user != null) {
                vo.setOwnerName(user.getRealName());
                vo.setOwnerPhone(user.getPhone());
            }
        }
        // 回复人
        if (c.getReplyBy() != null) {
            SysUser replier = sysUserMapper.selectById(c.getReplyBy());
            vo.setReplyByName(replier != null ? replier.getRealName() : "");
        }
        return vo;
    }
}
