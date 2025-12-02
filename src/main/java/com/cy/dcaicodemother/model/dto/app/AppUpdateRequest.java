package com.cy.dcaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改app请求
 */
@Data
public class AppUpdateRequest implements Serializable {

    private static final long serialVersionUID = 5505699867030686513L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

}
