package com.cy.dcaicodemother.model.dto.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增app请求
 */
@Data
public class AppAddRequest implements Serializable {

    private static final long serialVersionUID = 7583922116658032755L;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

}
