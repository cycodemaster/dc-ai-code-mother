package com.cy.dcaicodemother.ai.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 代码生成类型枚举
 */
@AllArgsConstructor
@Getter
public enum CodeGenTypeEnum {

    //HTML
    HTML("html", "原生HTML模式"),
    MULTI_File("multiFile", "原生多文件模式");

    private final String value;

    private final String desc;

    //根据value获取枚举值
    public static CodeGenTypeEnum getByValue(String value) {

        if (ObjUtil.isEmpty(value)){
            return null;
        }

        for (CodeGenTypeEnum codeGenTypeEnum : CodeGenTypeEnum.values()) {
            if (codeGenTypeEnum.getValue().equals(value)) {
                return codeGenTypeEnum;
            }
        }
        return null;
    }

}
