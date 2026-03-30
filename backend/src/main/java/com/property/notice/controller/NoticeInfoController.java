package com.property.notice.controller;

import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.notice.dto.NoticeAddDTO;
import com.property.notice.dto.NoticeQueryDTO;
import com.property.notice.service.NoticeInfoService;
import com.property.notice.vo.NoticeVO;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeInfoController {

    private final NoticeInfoService noticeInfoService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<NoticeVO>> page(NoticeQueryDTO dto) {
        return Result.success(noticeInfoService.page(dto));
    }

    @GetMapping("/published")
    public Result<PageResult<NoticeVO>> pagePublished(NoticeQueryDTO dto) {
        return Result.success(noticeInfoService.pagePublished(dto));
    }

    @GetMapping("/{id}")
    public Result<NoticeVO> getDetail(@PathVariable Long id) {
        return Result.success(noticeInfoService.getDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Long> add(@Valid @RequestBody NoticeAddDTO dto) {
        return Result.success(noticeInfoService.add(dto, SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeAddDTO dto) {
        noticeInfoService.update(id, dto);
        return Result.success();
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> publish(@PathVariable Long id) {
        noticeInfoService.publish(id, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/{id}/unpublish")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> unpublish(@PathVariable Long id) {
        noticeInfoService.unpublish(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> delete(@PathVariable Long id) {
        noticeInfoService.delete(id);
        return Result.success();
    }
}
