package com.cy.dcaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.cy.dcaicodemother.mapper")
public class DcAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(DcAiCodeMotherApplication.class, args);
    }

}
