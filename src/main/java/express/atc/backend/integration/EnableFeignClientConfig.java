package express.atc.backend.integration;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@EnableFeignClients(basePackages = {
        "express.atc.backend.integration.smsaero.client",
        "express.atc.backend.integration.cargoflow.client",
        "express.atc.backend.integration.cbrf.client",
        "express.atc.backend.integration.cfapi.client"
})
public class EnableFeignClientConfig {
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Логирует все: заголовки, тело, метаданные
    }

    @Bean
    public RequestInterceptor loggingRequestInterceptor() {
        return template -> {
            logFeignRequest(template);
        };
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) -> {
            log.error("Feign Client Error - Method: {}, Status: {}, Headers: {}, Body: {}",
                    methodKey,
                    response.status(),
                    response.headers(),
                    response.body());
            return new RuntimeException("Feign client error: " + response.status());
        };
    }

    private void logFeignRequest(RequestTemplate template) {
        log.info("=== FEIGN REQUEST ===");
        log.info("URL: {} {}", template.method(), template.url());
        log.info("Headers:");
        template.headers().forEach((key, values) ->
                values.forEach(value ->
                        log.info("  {}: {}", key, value)
                )
        );

        if (template.body() != null) {
            log.info("Body: {}", new String(template.body(), template.requestCharset()));
        }
        log.info("=====================");
    }
}
