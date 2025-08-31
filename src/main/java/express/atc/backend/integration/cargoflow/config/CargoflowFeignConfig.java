package express.atc.backend.integration.cargoflow.config;

import express.atc.backend.integration.cargoflow.exception.CargoflowApiException;
import express.atc.backend.integration.cargoflow.service.impl.CargoflowAuthManager;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CargoflowFeignConfig {

    private final CargoflowAuthManager authManager;

    @Bean
    public RequestInterceptor cargoflowAuthInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                try {
                    String token = authManager.getAccessToken();
                    template.header("Authorization", "Bearer " + token);
                    log.debug("Added Bearer token to Cargoflow request");
                } catch (Exception e) {
                    log.error("Failed to get auth token for Cargoflow request", e);
                    throw new RuntimeException("Authentication failed", e);
                }
            }
        };
    }

    @Bean
    public ErrorDecoder cargoflowErrorDecoder() {
        return (methodKey, response) -> {
            String message = String.format("Cargoflow API error - Status: %s, Method: %s",
                    response.status(), methodKey);

            return new CargoflowApiException(
                    message,
                    HttpStatus.valueOf(response.status()),
                    "Cargoflow service unavailable"
            );
        };
    }
}