package com.cy.dcaicodemother.parser;

import com.cy.dcaicodemother.ai.model.enums.CodeGenTypeEnum;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;

/**
 * 代码解析执行器
 */
public class CodeParserExecutor {

    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();
    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    /**
     * 代码解析
     * 根据不同的生成类型走不同的解析策略
     *
     * @param codeContent 待解析内容
     * @param codeGenTypeEnum 生成类型
     * @return 解析后的代码
     */
    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case HTML -> htmlCodeParser.parseCode(codeContent);
            case MULTI_File -> multiFileCodeParser.parseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenTypeEnum);
        };
    }

}
