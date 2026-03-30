package com.property.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.property.activity.dto.ActivityAddDTO;
import com.property.activity.dto.ActivityQueryDTO;
import com.property.activity.entity.ActivityInfo;
import com.property.activity.vo.ActivitySignupVO;
import com.property.activity.vo.ActivityVO;
import com.property.common.model.PageResult;

import java.util.List;

public interface ActivityService extends IService<ActivityInfo> {
    PageResult<ActivityVO> page(ActivityQueryDTO dto);
    PageResult<ActivityVO> pageForOwner(ActivityQueryDTO dto, Long ownerUserId);
    ActivityVO getDetail(Long id, Long ownerUserId);
    Long add(ActivityAddDTO dto, Long publisherId);
    void update(Long id, ActivityAddDTO dto);
    void cancel(Long id);
    void finish(Long id);
    void signup(Long activityId, Long ownerUserId);
    void cancelSignup(Long activityId, Long ownerUserId);
    List<ActivitySignupVO> listSignups(Long activityId);
}
