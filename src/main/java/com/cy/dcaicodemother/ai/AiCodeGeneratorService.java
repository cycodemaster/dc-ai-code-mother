package com.cy.dcaicodemother.ai;

import com.cy.dcaicodemother.ai.model.HtmlCodeResult;
import com.cy.dcaicodemother.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

public interface AiCodeGeneratorService {

    /**
     * 生成html代码
     *
     * @param userMessage 用户提示词
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 生成多文件代码
     *
     * @param userMessage 用户提示词
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-muti-file-system-prompt.txt")
    MultiFileCodeResult generateMutiFileCode(String userMessage);

    /**
     * 生成html代码，流式输出
     *
     * @param userMessage 用户提示词
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(String userMessage);

    /**
     * 生成多文件代码，流式输出
     *
     * @param userMessage 用户提示词
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-muti-file-system-prompt.txt")
    Flux<String> generateMutiFileCodeStream(String userMessage);

}
