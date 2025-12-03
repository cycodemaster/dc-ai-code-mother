package com.cy.dcaicodemother.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.cy.dcaicodemother.ai.model.enums.CodeGenTypeEnum;
import com.cy.dcaicodemother.exception.BusinessException;
import com.cy.dcaicodemother.exception.ErrorCode;
import com.cy.dcaicodemother.exception.ThrowUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 代码文件保存模板
 */
public abstract class CodeFileSaverTemplate<T> {

    //文件保存根目录
    protected static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 保存代码文件标准流程
     *
     * @param codeResult 代码结果
     * @param appId      应用id
     * @return 保存的目录
     */
    protected File saveCode(T codeResult, Long appId) {
        // 参数校验
        validateInput(codeResult);
        // 构建唯一文件夹路径
        String dirPath = buildUniqueDir(appId);
        // 保存代码文件
        saveFiles(codeResult, dirPath);
        // 返回文件路径
        return new File(dirPath);
    }

    /**
     * 参数校验
     *
     * @param codeResult 代码结果
     */
    protected void validateInput(T codeResult) {
        if (codeResult == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
        }
    }

    /**
     * 构建唯一文件夹路径
     *
     * @return 文件夹路径
     */
    protected String buildUniqueDir(Long appId) {
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR, "应用id不能为空");
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 单文件写入
     *
     * @param dirPath  文件夹路径
     * @param fileName 文件名
     * @param content  文件内容
     */
    protected static void writeToFile(String dirPath, String fileName, String content) {
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 获取代码类型
     *
     * @return 代码类型枚举
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存代码文件
     *
     * @param codeResult 代码结果
     * @param dirPath    文件夹路径
     */
    protected abstract void saveFiles(T codeResult, String dirPath);

}
