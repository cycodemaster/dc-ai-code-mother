package com.cy.dcaicodemother.saver;

import com.cy.dcaicodemother.ai.model.HtmlCodeResult;
import com.cy.dcaicodemother.ai.model.MultiFileCodeResult;
import com.cy.dcaicodemother.ai.model.enums.CodeGenTypeEnum;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;

import java.io.File;

/**
 * 文件保存执行器
 */
public class CodeFileSaverExecutor<T> {

    private final static HtmlCodeFileSaverTemplate htmlCodeFileSaverTemplate = new HtmlCodeFileSaverTemplate();
    private final static MultiFileCodeSaverTemplate multiFileCodeSaverTemplate = new MultiFileCodeSaverTemplate();

    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        return switch (codeGenTypeEnum) {
            case HTML -> htmlCodeFileSaverTemplate.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_File -> multiFileCodeSaverTemplate.saveCode((MultiFileCodeResult) codeResult, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenTypeEnum);
        };
    }

}
