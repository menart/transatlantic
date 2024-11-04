package express.atc.backend.integration.cargoflow.config;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.SSLException;
import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
public class CargoflowConfig {
    @Value("${cargoflow.cuba.rest.endpoint}")
    private String cargoflowAuthUrl;
    @Value("${cargoflow.cuba.rest.client.id}")
    private String clientId;
    @Value("${cargoflow.cuba.rest.client.secret}")
    private String clientSecret;

    @Value("${cargoflow.order.endpoint}")
    private String cargoflowOrderUrl;

    @Bean("cargoflowAuthWebClient")
    public WebClient cargoflowAuthWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .followRedirect(true)
                .wiretap(this.getClass().getCanonicalName(),
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

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

    @Bean("cargoflowOrderWebClient")
    public WebClient cargoflowOrderWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .followRedirect(true)
                .wiretap(this.getClass().getCanonicalName(),
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(cargoflowOrderUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .build();
    }
}
