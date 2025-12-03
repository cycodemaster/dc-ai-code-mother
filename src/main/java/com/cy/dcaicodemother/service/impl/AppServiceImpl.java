package com.cy.dcaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.cy.dcaicodemother.ai.model.enums.CodeGenTypeEnum;
import com.cy.dcaicodemother.core.AiCodeGeneratorFacade;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;
import com.cy.dcaicodemother.exception.ThrowUtils;
import com.cy.dcaicodemother.model.constant.AppConstant;
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
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
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
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

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
        if (CollUtil.isEmpty(appList)) {
            return CollUtil.newArrayList();
        }

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

    @Override
    public Flux<String> chatToGenCode(String userMessage, Long appId, User loginUser) {

        // 参数校验
        ThrowUtils.throwIf(appId == null || appId < 0, ErrorCode.PARAMS_ERROR, "应用id不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.PARAMS_ERROR, "用户不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(userMessage), ErrorCode.PARAMS_ERROR, "用户消息不能为空");

        // 仅应用创建人可生成应用
        App app = getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        ThrowUtils.throwIf(!loginUser.getId().equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");

        // 生成应用
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getByValue(app.getCodeGenType());
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        return aiCodeGeneratorFacade.generateAndSaveCodeStream(userMessage, codeGenTypeEnum, appId);

    }

    @Override
    public String deployApp(Long appId, User loginUser) {

        // 参数校验
        ThrowUtils.throwIf(appId == null || appId < 0, ErrorCode.PARAMS_ERROR, "应用id不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.PARAMS_ERROR, "用户不能为空");

        // 查询应用
        App app = getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        // 仅可部署自己创建的应用
        ThrowUtils.throwIf(!loginUser.getId().equals(app.getUserId()), ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");

        // 检查部署码是否已存在
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }

        // 检查代码源路径是否存在
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + app.getCodeGenType() + "_" + appId;
        File sourceDir = new File(sourceDirPath);
        ThrowUtils.throwIf(!sourceDir.exists() || !sourceDir.isDirectory(), ErrorCode.PARAMS_ERROR, "应用代码不存在，请先生成");

        // 复制应用代码到部署文件夹
        String deployDir = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDir), true);
        } catch (IORuntimeException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败：" + e.getMessage());
        }

        // 更新部署码，更新部署时间
        App newApp = new App();
        newApp.setId(appId);
        newApp.setDeployKey(deployKey);
        newApp.setDeployedTime(LocalDateTime.now());
        boolean result = this.updateById(newApp);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "部署失败");

        // 返回可访问的地址
        return String.format("%s/%s/", AppConstant.CODE_DEPLOY_DOMAIN, deployKey);
    }

}
