package express.atc.backend.integration.cargoflow.config;

import express.atc.backend.integration.cargoflow.service.impl.CargoflowAuthManager;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CargoflowAuthInterceptor implements RequestInterceptor {

    private final CargoflowAuthManager authManager;

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
}