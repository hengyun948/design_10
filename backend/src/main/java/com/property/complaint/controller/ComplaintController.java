package com.property.complaint.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.complaint.dto.ComplaintAddDTO;
import com.property.complaint.dto.ComplaintQueryDTO;
import com.property.complaint.service.ComplaintService;
import com.property.complaint.vo.ComplaintVO;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<ComplaintVO>> page(ComplaintQueryDTO dto) {
        return Result.success(complaintService.page(dto));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('OWNER')")
    public Result<PageResult<ComplaintVO>> myList(ComplaintQueryDTO dto) {
        dto.setOwnerId(SecurityUtils.getCurrentUserId());
        return Result.success(complaintService.myList(dto));
    }

    @GetMapping("/{id}")
    public Result<ComplaintVO> getDetail(@PathVariable Long id) {
        return Result.success(complaintService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OWNER')")
    public Result<Long> submit(@Valid @RequestBody ComplaintAddDTO dto) {
        return Result.success(complaintService.submit(dto, SecurityUtils.getCurrentUserId()));
    }

    @PostMapping("/{id}/handle")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> handle(@PathVariable Long id) {
        complaintService.handle(id, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/{id}/reply")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> reply(@PathVariable Long id, @RequestBody ReplyRequest req) {
        complaintService.reply(id, req.getReplyContent(), SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        complaintService.delete(id);
        return Result.success();
    }

    @Data
    static class ReplyRequest {
        private String replyContent;
    }
}
