package com.jinishop.jinishop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI jiniShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Jini Shop API")
                        .description("Jini Shop 백엔드 API")
                        .version("v1.0.0"));
    }

    // /api/**만 swagger에서 기본 그룹으로 문서화
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("jini-shop-public")
                .pathsToMatch("/api/**")
                .build();
    }
}