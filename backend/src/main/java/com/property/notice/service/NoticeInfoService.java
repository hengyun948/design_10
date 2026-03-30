package com.property.notice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.notice.dto.NoticeAddDTO;
import com.property.notice.dto.NoticeQueryDTO;
import com.property.notice.entity.NoticeInfo;
import com.property.notice.vo.NoticeVO;

public interface NoticeInfoService extends IService<NoticeInfo> {
    PageResult<NoticeVO> page(NoticeQueryDTO dto);
    PageResult<NoticeVO> pagePublished(NoticeQueryDTO dto);
    NoticeVO getDetail(Long id);
    Long add(NoticeAddDTO dto, Long publisherId);
    void update(Long id, NoticeAddDTO dto);
    void publish(Long id, Long publisherId);
    void unpublish(Long id);
    void delete(Long id);
}
