package com.property.log.aspect;

import com.property.log.annotation.OperationLog;
import com.property.log.service.OperationLogService;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysUserMapper;
import com.property.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final SysUserMapper sysUserMapper;

    @Around("@annotation(com.property.log.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        com.property.log.entity.OperationLog logEntity = new com.property.log.entity.OperationLog();
        int status = 1;
        String errorMsg = null;
        try {
            Object result = point.proceed();
            return result;
        } catch (Throwable e) {
            status = 0;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            try {
                long cost = System.currentTimeMillis() - start;
                MethodSignature sig = (MethodSignature) point.getSignature();
                Method method = sig.getMethod();
                OperationLog ann = method.getAnnotation(OperationLog.class);
                logEntity.setModuleName(ann.module());
                logEntity.setOperationType(ann.type());
                logEntity.setOperationDesc(ann.desc());
                logEntity.setExecuteTime(cost);
                logEntity.setStatus(status);
                logEntity.setErrorMsg(errorMsg);

                Long userId = SecurityUtils.getCurrentUserId();
                logEntity.setOperatorId(userId);
                if (userId != null) {
                    SysUser user = sysUserMapper.selectById(userId);
                    logEntity.setOperatorName(user != null ? user.getRealName() : "");
                }

                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    HttpServletRequest req = attrs.getRequest();
                    logEntity.setRequestUri(req.getRequestURI());
                    logEntity.setRequestMethod(req.getMethod());
                    logEntity.setIp(getClientIp(req));
                }
                operationLogService.saveLog(logEntity);
            } catch (Exception ignored) {}
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
