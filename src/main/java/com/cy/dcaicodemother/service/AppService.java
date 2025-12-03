package com.cy.dcaicodemother.service;

import com.cy.dcaicodemother.model.dto.app.AppQueryRequest;
import com.cy.dcaicodemother.model.entity.User;
import com.cy.dcaicodemother.model.vo.app.AppVO;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.cy.dcaicodemother.model.entity.App;
import reactor.core.publisher.Flux;

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

    /**
     * 根据提示词生成应用（流式SSE）
     *
     * @param userMessage 用户提示词
     * @param appId       应用id
     * @param loginUser   登录用户
     * @return 流式输出
     */
    Flux<String> chatToGenCode(String userMessage, Long appId, User loginUser);

    /**
     * 部署应用
     *
     * @param appId     应用id
     * @param loginUser 登录用户
     * @return 应用访问路径
     */
    String deployApp(Long appId, User loginUser);
}
