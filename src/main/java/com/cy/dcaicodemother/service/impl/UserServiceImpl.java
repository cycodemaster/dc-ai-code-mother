package com.cy.dcaicodemother.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;
import com.cy.dcaicodemother.model.dto.user.UserRegisterRequest;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.cy.dcaicodemother.model.entity.User;
import com.cy.dcaicodemother.mapper.UserMapper;
import com.cy.dcaicodemother.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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


}
