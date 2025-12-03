package com.cy.dcaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 部署请求类
 */
@Data
public class AppDeployRequest implements Serializable {

    private static final long serialVersionUID = -6096611282309394065L;

    /**
     * 应用 id
     */
    private Long appId;
}
