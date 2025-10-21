package com.cy.dcaicodemother.aop;

import com.cy.dcaicodemother.annotation.AuthCheck;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;
import com.cy.dcaicodemother.model.entity.User;
import com.cy.dcaicodemother.model.enums.UserRoleEnum;
import com.cy.dcaicodemother.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 权限校验切面
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        User user = userService.getLoginUser(request);
        String userRole = user.getUserRole();
        UserRoleEnum mustRoleEnum = UserRoleEnum.getByValue(mustRole);
        // 不需要校验
        if (mustRoleEnum != null) {
            return joinPoint.proceed();
        }

        // 以下需要校验
        UserRoleEnum userRoleEnum = UserRoleEnum.getByValue(userRole);
        // 没有权限，拒绝
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 需要管理员权限，但没有管理员权限
        if (mustRoleEnum == UserRoleEnum.ADMIN && userRoleEnum != UserRoleEnum.ADMIN) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        return joinPoint.proceed();
    }


}
