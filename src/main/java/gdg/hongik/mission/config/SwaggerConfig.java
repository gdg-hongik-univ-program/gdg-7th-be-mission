package gdg.hongik.mission.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GDG Hongik Mission API")
                        .description("사용자 상품 조회·구매 및 관리자 상품·재고 관리 API 명세서")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server().url("/").description("현재 서버 (로컬 및 배포 환경 자동 대응)")));
    }
}