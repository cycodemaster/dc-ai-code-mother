package com.cy.dcaicodemother.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求类
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -1771921087780216978L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认校验
     */
    private String checkPassword;

}
