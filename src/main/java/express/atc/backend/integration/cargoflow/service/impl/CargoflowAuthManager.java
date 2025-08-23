package express.atc.backend.integration.cargoflow.service.impl;

import express.atc.backend.exception.CargoflowException;
import express.atc.backend.integration.cargoflow.dto.CargoflowToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CargoflowAuthManager {

    private final HttpClient httpClient;

    @Value("${cargoflow.cuba.rest.endpoint}")
    private String cargoflowAuthUrl;
    @Value("${cargoflow.cuba.rest.client.id}")
    private String clientId;
    @Value("${cargoflow.cuba.rest.client.secret}")
    private String clientSecret;

    @Value("${cargoflow.password}")
    private String cargoflowPassword;
    @Value("${cargoflow.username}")
    private String cargoflowUsername;

    private volatile CargoflowToken cargoflowToken;

    public WebClient cargoflowAuthWebClient() throws SSLException {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(cargoflowAuthUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    httpHeaders.setAcceptCharset(List.of());
                    httpHeaders.setBasicAuth(clientId, clientSecret);
                })
                .build();
    }

    public String getAccessToken() {
        if (cargoflowToken == null || isTokenExpired()) {
            synchronized (this) {
                if (cargoflowToken == null || isTokenExpired()) {
                    cargoflowToken = fetchNewToken();
                }
            }
        }
        return cargoflowToken.getAccessToken();
    }

    private boolean isTokenExpired() {
        return LocalDateTime.now().isAfter(cargoflowToken.getExpiresDateTime());
    }

    CargoflowToken fetchNewToken() {
        try {
            CargoflowToken newToken = cargoflowAuthWebClient().post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "password")
                            .with("username", cargoflowUsername)
                            .with("password", cargoflowPassword))
                    .retrieve()
                    .bodyToMono(CargoflowToken.class)
                    .block();

            if (newToken == null) {
                throw new CargoflowException("Failed to authenticate with Cargoflow");
            }
            newToken.setExpiresDateTime(LocalDateTime.now().plusSeconds(newToken.getExpiresIn()));
            return newToken;
        } catch (Exception e) {
            throw new CargoflowException("Error fetching new token: " + e.getMessage());
        }
    }

    public void invalidateToken() {
        synchronized (this) {
            cargoflowToken = null;
        }
    }
}