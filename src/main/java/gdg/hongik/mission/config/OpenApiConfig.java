package gdg.hongik.mission.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("GDG Hongik Mission API Specification")
                .description("소비자 및 관리자용 상품 관리 API 명세서입니다.")
                .version("v1.0.0");

        return new OpenAPI()
                .info(info);
    }
}