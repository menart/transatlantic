package express.atc.backend.integration.cargoflow.config;

import express.atc.backend.integration.cargoflow.exception.CargoflowApiException;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
@EnableFeignClients(basePackages = "express.atc.backend.integration.cargoflow.client")
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
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