package express.atc.backend.integration.smsaero.config;

import feign.auth.BasicAuthRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SmsAeroFeignConfig {

    @Value("${sms-aero.login}")
    private String smsAeroLogin;

    @Value("${sms-aero.token}")
    private String smsAeroToken;

    // @Bean
    // public BasicAuthRequestInterceptor basicSmsAeroAuthRequestInterceptor() {
    //     return new BasicAuthRequestInterceptor(smsAeroLogin, smsAeroToken);
    // }
}