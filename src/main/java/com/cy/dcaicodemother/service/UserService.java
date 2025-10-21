package com.cy.dcaicodemother.service;

import com.cy.dcaicodemother.model.dto.user.UserLoginRequest;
import com.cy.dcaicodemother.model.dto.user.UserQueryRequest;
import com.cy.dcaicodemother.model.dto.user.UserRegisterRequest;
import com.cy.dcaicodemother.model.vo.user.LoginUserVO;
import com.cy.dcaicodemother.model.vo.user.UserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.cy.dcaicodemother.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author 大陈
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param registerRequest 用户注册请求
     * @return 新用户id
     */
    long userRegister(UserRegisterRequest registerRequest);

    /**
     * 密码加密
     *
     * @param userPassword 原密码
     * @return 加密后密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 用户登录
     *
     * @param loginRequest 用户登录请求
     * @return 用户
     */
    LoginUserVO userLogin(UserLoginRequest loginRequest, HttpServletRequest request);

    /**
     * 获取脱敏的登录用户信息
     *
     * @param user 原始用户
     * @return 脱敏后用户
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户登出
     *
     * @param request 请求
     * @return 登出结果
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 获取脱敏的用户信息
     *
     * @param user 原始用户
     * @return 脱敏后用户
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏的用户列表
     *
     * @param userList 原始用户列表
     * @return 脱敏的用户列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 转换为QueryWrapper对象
     *
     * @param userQueryRequest 用户请求
     * @return QueryWrapper对象
     */
    QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest);

}
