package express.atc.backend.integration.cfapi.config;

import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Configuration
@Slf4j
@EnableFeignClients(basePackages = "express.atc.backend.integration.cfapi.client")
public class CfApiClientConfig {

    @Bean
    public ErrorDecoder cfApiErrorDecoder() {
        return (methodKey, response) -> {
            log.error("HTTP Error {}: {}", response.status(), response.body());
            return new ResponseStatusException(
                    HttpStatus.valueOf(response.status()),
                    "CF API " + methodKey + " request failed: " + response.body()
            );
        };
    }
}