package express.atc.backend.integration.cargoflow.service.impl;

import express.atc.backend.exception.CargoflowException;
import express.atc.backend.integration.cargoflow.dto.CargoflowToken;
import express.atc.backend.integration.cargoflow.service.CargoflowAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CargoflowAuthServiceImpl implements CargoflowAuthService {

    private static final String CARGOFLOW_GRAND_TYPE = "password";
    private CargoflowToken cargoflowToken;
    private final WebClient cargoflowAuthWebClient;
    @Value("${cargoflow.password}")
    private String cargoflowPassword;
    @Value("${cargoflow.username}")
    private String cargoflowUsername;

    @Override
    public String getToken() {
        return getSavedToken().orElseGet(this::getNewToken);
    }

    private Optional<String> getSavedToken() {
        return cargoflowToken != null
                && LocalDateTime.now().isBefore(cargoflowToken.getExpiresDateTime())
                ? Optional.of(cargoflowToken.getAccessToken())
                : Optional.empty();
    }

    private String getNewToken() {
        try {
            cargoflowToken = cargoflowAuthWebClient
                    .post()
                    .body(BodyInserters.fromFormData("grant_type", CARGOFLOW_GRAND_TYPE)
                            .with("username", cargoflowUsername)
                            .with("password", cargoflowPassword))
                    .retrieve()
                    .bodyToMono(CargoflowToken.class)
                    .block();
            if (Objects.isNull(cargoflowToken)) {
                throw new CargoflowException("Не удалось авторизоваться в Cargoflow");
            }
            cargoflowToken.setExpiresDateTime(LocalDateTime.now().plusSeconds(cargoflowToken.getExpiresIn()));
            return cargoflowToken.getAccessToken();
        } catch (Exception exception) {
            throw new CargoflowException(exception.getMessage());
        }
    }
}
