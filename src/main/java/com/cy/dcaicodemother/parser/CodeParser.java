package com.cy.dcaicodemother.parser;

/**
 * 代码解析器策略接口
 * @param <T>
 */
public interface CodeParser<T> {

    /**
     * 解析代码
     * @param codeContent 待解析内容
     * @return 解析结果
     */
    T parseCode(String codeContent);
}
