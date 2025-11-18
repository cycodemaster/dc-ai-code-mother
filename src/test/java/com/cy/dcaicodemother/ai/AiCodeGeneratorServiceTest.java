package com.cy.dcaicodemother.ai;

import com.cy.dcaicodemother.ai.model.HtmlCodeResult;
import com.cy.dcaicodemother.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("请生成一个20行代码内的登录页面");
        Assertions.assertNotNull(result);
    }

    @Test
    void generateMutiFileCode() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMutiFileCode("请生成一个20行代码内的博客页面");
        Assertions.assertNotNull(result);
    }
}