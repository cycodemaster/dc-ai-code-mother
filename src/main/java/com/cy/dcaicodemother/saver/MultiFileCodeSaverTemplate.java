package com.cy.dcaicodemother.saver;

import cn.hutool.core.util.StrUtil;
import com.cy.dcaicodemother.ai.model.MultiFileCodeResult;
import com.cy.dcaicodemother.ai.model.enums.CodeGenTypeEnum;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;

/**
 * 多文件代码保存器模板
 */
public class MultiFileCodeSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_File;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult codeResult, String dirPath) {
        writeToFile(dirPath, "index.html", codeResult.getHtmlCode());
        writeToFile(dirPath, "style.css", codeResult.getCssCode());
        writeToFile(dirPath, "script.js", codeResult.getJsCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult codeResult) {
        super.validateInput(codeResult);
        if (StrUtil.isBlank(codeResult.getHtmlCode())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码不能为空");
        }
    }
}
