package com.property.complaint.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.common.model.PageResult;
import com.property.complaint.dto.ComplaintAddDTO;
import com.property.complaint.dto.ComplaintQueryDTO;
import com.property.complaint.entity.ComplaintSuggestion;
import com.property.complaint.vo.ComplaintVO;

public interface ComplaintService extends IService<ComplaintSuggestion> {
    PageResult<ComplaintVO> page(ComplaintQueryDTO dto);
    PageResult<ComplaintVO> myList(ComplaintQueryDTO dto);
    ComplaintVO getDetail(Long id);
    Long submit(ComplaintAddDTO dto, Long userId);
    void handle(Long id, Long operatorId);
    void reply(Long id, String replyContent, Long operatorId);
    void delete(Long id);
}
