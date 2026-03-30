package com.property.activity.controller;

import com.property.activity.dto.ActivityAddDTO;
import com.property.activity.dto.ActivityQueryDTO;
import com.property.activity.service.ActivityService;
import com.property.activity.vo.ActivitySignupVO;
import com.property.activity.vo.ActivityVO;
import com.property.common.api.Result;
import com.property.common.model.PageResult;
import com.property.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<PageResult<ActivityVO>> page(ActivityQueryDTO dto) {
        return Result.success(activityService.page(dto));
    }

    @GetMapping("/published")
    public Result<PageResult<ActivityVO>> pagePublished(ActivityQueryDTO dto) {
        return Result.success(activityService.pageForOwner(dto, SecurityUtils.getCurrentUserId()));
    }

    @GetMapping("/{id}")
    public Result<ActivityVO> getDetail(@PathVariable Long id) {
        return Result.success(activityService.getDetail(id, SecurityUtils.getCurrentUserId()));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Long> add(@Valid @RequestBody ActivityAddDTO dto) {
        return Result.success(activityService.add(dto, SecurityUtils.getCurrentUserId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ActivityAddDTO dto) {
        activityService.update(id, dto);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> cancel(@PathVariable Long id) {
        activityService.cancel(id);
        return Result.success();
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<Void> finish(@PathVariable Long id) {
        activityService.finish(id);
        return Result.success();
    }

    @PostMapping("/{id}/signup")
    @PreAuthorize("hasAuthority('OWNER')")
    public Result<Void> signup(@PathVariable Long id) {
        activityService.signup(id, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @PostMapping("/{id}/cancelSignup")
    @PreAuthorize("hasAuthority('OWNER')")
    public Result<Void> cancelSignup(@PathVariable Long id) {
        activityService.cancelSignup(id, SecurityUtils.getCurrentUserId());
        return Result.success();
    }

    @GetMapping("/{id}/signups")
    @PreAuthorize("hasAnyAuthority('ADMIN','PROPERTY_STAFF')")
    public Result<List<ActivitySignupVO>> listSignups(@PathVariable Long id) {
        return Result.success(activityService.listSignups(id));
    }
}
