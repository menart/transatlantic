package express.atc.backend.config;

import express.atc.backend.enums.convert.StringToEnumConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${project.url}")
    private String ourServer;

    @Bean
    public List<String> publicEndpoints() {
        return Arrays.asList(
                "/actuator/**",
                "/api/auth/**",
                "/swagger-ui/**",
                "/swagger-resources/*",
                "/v3/**",
                "/api/tracking/find/**",
                "/api/calculate/**",
                "/api/payment/**",
                "/api/landing/**",
                "/api/payment/ctrl"
        );
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToEnumConverter());
    }
}