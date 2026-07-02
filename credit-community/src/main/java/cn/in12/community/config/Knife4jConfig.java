package cn.in12.community.config;

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
                        .title("创新学分申领管理平台-社区模块接口文档")
                        .version("1.0")
                        .description("提供学生发布求助、查看求助以及管理员管理帖子等功能接口")
                        .contact(new Contact().name("in12").email("support@in12.cn")));
    }
}
