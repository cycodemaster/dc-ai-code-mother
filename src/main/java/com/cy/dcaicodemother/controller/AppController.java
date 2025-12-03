package com.cy.dcaicodemother.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cy.dcaicodemother.ai.model.enums.CodeGenTypeEnum;
import com.cy.dcaicodemother.annotation.AuthCheck;
import com.cy.dcaicodemother.common.BaseResponse;
import com.cy.dcaicodemother.common.DeleteRequest;
import com.cy.dcaicodemother.common.ResultUtils;
import com.cy.dcaicodemother.exception.ErrorCode;
import com.cy.dcaicodemother.exception.ThrowUtils;
import com.cy.dcaicodemother.model.constant.AppConstant;
import com.cy.dcaicodemother.model.constant.UserConstant;
import com.cy.dcaicodemother.model.dto.app.AppAddRequest;
import com.cy.dcaicodemother.model.dto.app.AppAdminUpdateRequest;
import com.cy.dcaicodemother.model.dto.app.AppQueryRequest;
import com.cy.dcaicodemother.model.dto.app.AppUpdateRequest;
import com.cy.dcaicodemother.model.entity.User;
import com.cy.dcaicodemother.model.vo.app.AppVO;
import com.cy.dcaicodemother.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.cy.dcaicodemother.model.entity.App;
import com.cy.dcaicodemother.service.AppService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用 控制层。
 *
 * @author 大陈
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    private AppService appService;
    @Resource
    private UserService userService;

    /**
     * 创建应用
     *
     * @param appAddRequest 创建应用请求
     * @param request       请求
     * @return 应用id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {

        // 参数校验
        ThrowUtils.throwIf(appAddRequest == null, ErrorCode.PARAMS_ERROR);
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "应用初始化 prompt 不能为空");

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 构建数据并保存
        App app = new App();
        BeanUtil.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());
        // 应用名称暂时为prompt前12位
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        // 暂时设置为多文件生成
        app.setCodeGenType(CodeGenTypeEnum.MULTI_File.getValue());

        // 保存应用
        boolean result = appService.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(app.getId());
    }

    /**
     * 更新应用信息
     *
     * @param appUpdateRequest 应用更新请求
     * @param request          请求
     * @return 更新结果
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, HttpServletRequest request) {

        // 参数校验
        ThrowUtils.throwIf(appUpdateRequest == null || appUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        // 应用创建人才可更新应用信息
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        App oldApp = appService.getById(appUpdateRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!oldApp.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);

        // 更新应用信息
        App app = new App();
        app.setId(appUpdateRequest.getId());
        app.setAppName(appUpdateRequest.getAppName());
        app.setEditTime(LocalDateTime.now());
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);

    }

    /**
     * 删除应用
     *
     * @param deleteRequest 删除应用请求
     * @param request       请求
     * @return 删除结果
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {

        // 参数校验：检查删除请求参数和ID是否为空
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        // 仅创建人/管理员可删除
        User loginUser = userService.getLoginUser(request);
        App oldApp = appService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!oldApp.getUserId().equals(loginUser.getId()) && !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole()), ErrorCode.NO_AUTH_ERROR);

        // 删除应用
        boolean result = appService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据id获取应用详情
     *
     * @param id 应用id
     * @return 应用封装类
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVOById(long id) {

        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取应用信息
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取封装类
        return ResultUtils.success(appService.getAppVO(app));

    }

    /**
     * 分页查询我的应用
     *
     * @param appQueryRequest 应用查询请求
     * @param request         请求
     * @return 应用列表
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AppVO>> listMyAppVOByPage(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request) {

        // 参数校验
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 最多查询20条数据
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "最多查询20条数据");

        // 组装请求
        // 仅可查看自己创建的应用
        appQueryRequest.setUserId(loginUser.getId());
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);

        // 分页查询应用
        int pageNum = appQueryRequest.getPageNum();
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);

        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 分页查询精选应用
     *
     * @param appQueryRequest 应用查询请求
     * @return 应用列表
     */
    @PostMapping("/good/list/page/vo")
    public BaseResponse<Page<AppVO>> listGoodAppVOByPage(@RequestBody AppQueryRequest appQueryRequest) {

        // 参数校验
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 最多查询20条数据
        int pageSize = appQueryRequest.getPageSize();
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR, "最多查询20条数据");

        // 组装请求
        appQueryRequest.setPriority(AppConstant.GOOD_APP_PRIORITY);
        QueryWrapper queryWrapper = appService.getQueryWrapper(appQueryRequest);

        // 分页查询应用
        int pageNum = appQueryRequest.getPageNum();
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), queryWrapper);

        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 管理员删除应用
     *
     * @param deleteRequest 删除应用请求
     * @return 删除结果
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteAppByAdmin(@RequestBody DeleteRequest deleteRequest) {

        // 参数校验：检查删除请求参数和ID是否为空
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        // 校验应用是否存在
        App oldApp = appService.getById(deleteRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);

        // 删除应用
        boolean result = appService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 管理员更新应用请求
     * @return 更新结果
     */
    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateAppByAdmin(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {

        // 参数校验
        ThrowUtils.throwIf(appAdminUpdateRequest == null || appAdminUpdateRequest.getId() < 0, ErrorCode.PARAMS_ERROR);

        // 查询应用是否存在
        App oldApp = appService.getById(appAdminUpdateRequest.getId());
        ThrowUtils.throwIf(oldApp == null, ErrorCode.NOT_FOUND_ERROR);

        // 更新应用信息
        App app = new App();
        BeanUtils.copyProperties(appAdminUpdateRequest, app);
        app.setEditTime(LocalDateTime.now());
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 管理员分页查询应用
     *
     * @param appQueryRequest 应用查询请求
     * @return 应用列表
     */
    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppVO>> listAppVOByPageByAdmin(@RequestBody AppQueryRequest appQueryRequest) {

        // 参数校验
        ThrowUtils.throwIf(appQueryRequest == null, ErrorCode.PARAMS_ERROR);

        // 分页查询应用
        int pageNum = appQueryRequest.getPageNum();
        int pageSize = appQueryRequest.getPageSize();
        Page<App> appPage = appService.page(Page.of(pageNum, pageSize), appService.getQueryWrapper(appQueryRequest));

        // 数据封装
        Page<AppVO> appVOPage = new Page<>(pageNum, pageSize, appPage.getTotalRow());
        List<AppVO> appVOList = appService.getAppVOList(appPage.getRecords());
        appVOPage.setRecords(appVOList);
        return ResultUtils.success(appVOPage);
    }

    /**
     * 管理员根据id获取应用详情
     *
     * @param id 应用id
     * @return 应用封装类
     */
    @GetMapping("/admin/get/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppVO> getAppVOByIdByAdmin(long id) {

        // 参数校验
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);

        // 获取应用信息
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);

        // 获取封装类
        return ResultUtils.success(appService.getAppVO(app));

    }

}
