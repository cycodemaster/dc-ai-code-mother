package com.cy.dcaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;
import com.cy.dcaicodemother.model.dto.user.UserLoginRequest;
import com.cy.dcaicodemother.model.dto.user.UserRegisterRequest;
import com.cy.dcaicodemother.model.vo.user.LoginUserVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.cy.dcaicodemother.model.entity.User;
import com.cy.dcaicodemother.mapper.UserMapper;
import com.cy.dcaicodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.cy.dcaicodemother.model.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author 大陈
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public long userRegister(UserRegisterRequest registerRequest) {

        //1、校验
        if (registerRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

        //2、判断用户是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.mapper.selectCountByQuery(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
        }

        //3、密码加密
        String encryptPassword = getEncryptPassword(userPassword);

        //4、保存用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("无名");
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户注册失败，数据库错误");
        }
        return user.getId();
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "dachen";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
    }

    @Override
    public LoginUserVO userLogin(UserLoginRequest loginRequest, HttpServletRequest request) {

        // 校验
        if (loginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();
        if (StrUtil.hasBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 密码加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 查询用户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);

        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return getLoginUserVO(user);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null || user.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 根据id查询用户
        User currentUser = this.getById(user.getId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return currentUser;

    }

    public boolean userLogout(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

}
