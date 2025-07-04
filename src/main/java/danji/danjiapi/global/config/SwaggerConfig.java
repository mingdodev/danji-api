package danji.danjiapi.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "단지 API 명세서",
                description = "단지(단체 주문 지키미)의 비즈니스 로직에 대한 API 명세서입니다."
        )
)
@Configuration
public class SwaggerConfig {
}
