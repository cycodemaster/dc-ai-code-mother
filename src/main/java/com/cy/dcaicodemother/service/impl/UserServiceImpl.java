package com.cy.dcaicodemother.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.cy.dcaicodemother.model.entity.User;
import com.cy.dcaicodemother.mapper.UserMapper;
import com.cy.dcaicodemother.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author 大陈
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}
