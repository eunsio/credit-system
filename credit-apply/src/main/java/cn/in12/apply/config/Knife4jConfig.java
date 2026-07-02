package cn.in12.apply.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("创新学分申领管理平台-申领模块接口文档")
                        .version("1.0")
                        .description("提供学生申领学分、查看学分、获奖统计以及管理员审批等核心功能接口")
                        .contact(new Contact().name("in12").email("support@in12.cn")));
    }
}
