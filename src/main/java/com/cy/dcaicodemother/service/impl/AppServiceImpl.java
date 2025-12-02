package com.cy.dcaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.cy.dcaicodemother.exception.ErrorCode;
import com.cy.dcaicodemother.exception.ThrowUtils;
import com.cy.dcaicodemother.model.dto.app.AppQueryRequest;
import com.cy.dcaicodemother.model.entity.User;
import com.cy.dcaicodemother.model.vo.app.AppVO;
import com.cy.dcaicodemother.model.vo.user.UserVO;
import com.cy.dcaicodemother.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.cy.dcaicodemother.model.entity.App;
import com.cy.dcaicodemother.mapper.AppMapper;
import com.cy.dcaicodemother.service.AppService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author 大陈
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    @Resource
    private UserService userService;

    @Override
    public AppVO getAppVO(App app) {

        // 参数校验
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);

        // 转换
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);

        // 组装userVO
        if (app.getUserId() != null) {
            User user = userService.getById(app.getUserId());
            if (user != null) {
                appVO.setUser(userService.getUserVO(user));
            }
        }

        return appVO;
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {

        // 参数校验
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 组装条件
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();

        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));

    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        // 参数校验
        ThrowUtils.throwIf(CollectionUtil.isEmpty(appList), ErrorCode.PARAMS_ERROR);

        // 批量查询创建用户信息
        Set<Long> userIds = appList.stream().map(App::getUserId).collect(Collectors.toSet());
        Map<Long, UserVO> userMap = userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, userService::getUserVO));

        // 组装数据
        return appList.stream().map(app -> {
            AppVO appVO = new AppVO();
            BeanUtil.copyProperties(app, appVO);
            appVO.setUser(userMap.get(app.getUserId()));
            return appVO;
        }).collect(Collectors.toList());

    }

}
