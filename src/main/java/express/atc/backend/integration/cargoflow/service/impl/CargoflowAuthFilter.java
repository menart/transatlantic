package express.atc.backend.integration.cargoflow.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CargoflowAuthFilter implements ExchangeFilterFunction {

    private final CargoflowAuthManager authManager;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return retryFilter(request, next, 1);
    }

    private Mono<ClientResponse> retryFilter(ClientRequest request, ExchangeFunction next, int retryCount) {
        if (retryCount > 2) {
            return next.exchange(request)
                    .flatMap(response -> response.statusCode() == HttpStatus.UNAUTHORIZED
                            ? Mono.error(new RuntimeException("Authorization failed after retries"))
                            : Mono.just(response));
        }

        return Mono.defer(() -> {
            String token = authManager.getAccessToken();
            ClientRequest newRequest = ClientRequest.from(request)
                    .headers(headers -> headers.setBearerAuth(token))
                    .build();

            return next.exchange(newRequest)
                    .flatMap(response -> {
                        if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
                            authManager.invalidateToken();
                            return retryFilter(request, next, retryCount + 1);
                        }
                        return Mono.just(response);
                    });
        });
    }
}