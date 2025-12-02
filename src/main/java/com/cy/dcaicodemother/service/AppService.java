package com.cy.dcaicodemother.service;

import com.cy.dcaicodemother.model.dto.app.AppQueryRequest;
import com.cy.dcaicodemother.model.vo.app.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.cy.dcaicodemother.model.entity.App;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author 大陈
 */
public interface AppService extends IService<App> {

    /**
     * 补充app关联信息
     *
     * @param app 应用实体
     * @return appVO
     */
    AppVO getAppVO(App app);

    /**
     * 根据查询条件组装查询条件包装器
     *
     * @param appQueryRequest 应用查询请求
     * @return 查询条件包装器
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 批量补充app关联信息
     *
     * @param appList 应用列表
     * @return appVO列表
     */
    List<AppVO> getAppVOList(List<App> appList);
}
