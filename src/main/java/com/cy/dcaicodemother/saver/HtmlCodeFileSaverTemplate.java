package com.cy.dcaicodemother.saver;

import cn.hutool.core.util.StrUtil;
import com.cy.dcaicodemother.ai.model.HtmlCodeResult;
import com.cy.dcaicodemother.ai.model.MultiFileCodeResult;
import com.cy.dcaicodemother.ai.model.enums.CodeGenTypeEnum;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult codeResult, String dirPath) {
        writeToFile(dirPath, "index.html", codeResult.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult codeResult) {
        super.validateInput(codeResult);
        if (StrUtil.isBlank(codeResult.getHtmlCode())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码不能为空");
        }
    }

}
