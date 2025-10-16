package com.cy.dcaicodemother.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户角色
 */
@Getter
@AllArgsConstructor
public enum UserRoleEnum {

    ADMIN("管理员", "admin"),
    USER("普通用户", "user");

    private final String text;
    private final String value;

    /**
     * 根据值获取对应的用户角色枚举
     *
     * @param value 要匹配的值
     * @return 匹配到的用户角色枚举，如果没有匹配则返回null
     */
    public static UserRoleEnum getByValue(String value) {

        if (ObjUtil.isEmpty(value)){
            return null;
        }

        for (UserRoleEnum roleEnum : UserRoleEnum.values()) {
            if (roleEnum.getValue().equals(value)) {
                return roleEnum;
            }
        }
        return null;
    }

}
