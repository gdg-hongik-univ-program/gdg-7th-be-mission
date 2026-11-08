package gdg.hongik.mission.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("쇼핑몰 API")
                .version("1.0.0")
                .description("상품 조회, 구매 및 관리자용 상품 관리 API");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
