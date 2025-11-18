package com.cy.dcaicodemother.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.cy.dcaicodemother.ai.model.HtmlCodeResult;
import com.cy.dcaicodemother.ai.model.MultiFileCodeResult;
import com.cy.dcaicodemother.ai.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 代码文件保存器
 */
public class CodeFileSaver {

    //文件保存根目录
    public static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 保存单文件生成结果
     * @param result 单文件生成结果
     * @return 保存的目录
     */
    public static File saveHtmlCodeResult(HtmlCodeResult result) {
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 保存多文件生成结果
     * @param result 多文件生成结果
     * @return 保存的目录
     */
    public static File saveMutiFileResult(MultiFileCodeResult result){
        String baseDirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_File.getValue());
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        writeToFile(baseDirPath, "style.css", result.getHtmlCode());
        writeToFile(baseDirPath, "script.js", result.getHtmlCode());
        return new File(baseDirPath);
    }

    /**
     * 构建唯一文件夹路径
     * @param bizType 代码生成类型
     * @return 文件夹路径
     */
    public static String buildUniqueDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 单文件写入
     * @param dirPath 文件夹路径
     * @param fileName 文件名
     * @param content 文件内容
     */
    public static void writeToFile(String dirPath, String fileName, String content) {
        String filePath = dirPath + File.separator + fileName;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

}
