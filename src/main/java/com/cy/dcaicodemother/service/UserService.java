package com.cy.dcaicodemother.service;

import com.cy.dcaicodemother.model.dto.user.UserRegisterRequest;
import com.mybatisflex.core.service.IService;
import com.cy.dcaicodemother.model.entity.User;

/**
 * 用户 服务层。
 *
 * @author 大陈
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param registerRequest 用户注册请求
     * @return 新用户id
     */
    long userRegister(UserRegisterRequest registerRequest);

    /**
     * 密码加密
     * @param userPassword 原密码
     * @return 加密后密码
     */
    String getEncryptPassword(String userPassword);
}
