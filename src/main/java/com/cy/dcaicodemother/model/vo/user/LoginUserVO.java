package com.cy.dcaicodemother.model.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 脱敏的登录用户信息
 */
@Data
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = -5257525868606644033L;
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 编辑时间
     */
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
