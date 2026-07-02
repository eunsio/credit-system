package cn.in12.competition.config;

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
                        .title("创新学分申领管理平台-比赛模块接口文档")
                        .version("1.0")
                        .description("提供比赛信息的发布、修改、删除及学生查询等相关接口")
                        .contact(new Contact().name("in12").email("support@in12.cn")));
    }
}
