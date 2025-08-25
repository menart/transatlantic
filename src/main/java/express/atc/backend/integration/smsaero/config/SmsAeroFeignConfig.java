package express.atc.backend.integration.smsaero.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@EnableFeignClients(basePackages = "express.atc.backend.integration.smsaero.client")
public class SmsAeroFeignConfig {

    @Value("${sms-aero.login}")
    private String smsAeroLogin;

    @Value("${sms-aero.token}")
    private String smsAeroToken;

    @Bean
    public RequestInterceptor smsAeroAuthInterceptor() {
        return template -> {
            String credentials = STR."\{smsAeroLogin}:\{smsAeroToken}";
            String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            template.header("Authorization", STR."Basic \{base64Credentials}");
        };
    }
}