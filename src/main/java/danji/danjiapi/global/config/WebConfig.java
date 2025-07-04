package danji.danjiapi.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures global Cross-Origin Resource Sharing (CORS) settings for the application.
     *
     * Applies CORS configuration to all URL paths, allowing any origin pattern, specific HTTP methods (GET, POST, PUT, DELETE, OPTIONS), and headers ("Authorization", "Content-Type"). Exposes the "Authorization" header to clients and enables credentials in cross-origin requests.
     *
     * @param registry the CorsRegistry to configure CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .exposedHeaders("Authorization")
                .allowCredentials(true);
    }
}