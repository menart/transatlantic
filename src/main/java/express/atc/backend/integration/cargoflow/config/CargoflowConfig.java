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
    @Value("${cargoflow.entity.endpoint}")
    private String cargoflowEntityUrl;
    @Value("${cargoflow.upload.endpoint}")
    private String cargoflowUploadUrl;
    @Value("${cargoflow.upload.attach}")
    private String cargoflowAttachUrl;
    @Value("${cargoflow.entity.orders}")
    private String cargoflowListOrderUrl;


    @Bean
    public HttpClient httpClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        return HttpClient.create()
                .secure(t -> t.sslContext(sslContext))
                .followRedirect(true)
                .wiretap(this.getClass().getCanonicalName(),
                        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
    }

    @Bean("cargoflowAuthWebClient")
    public WebClient cargoflowAuthWebClient() throws SSLException {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(cargoflowAuthUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    httpHeaders.setAcceptCharset(List.of());
                    httpHeaders.setBasicAuth(clientId, clientSecret);
                })
                .build();
    }

    @Bean("cargoflowEntityWebClient")
    public WebClient cargoflowOrderWebClient() throws SSLException {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(cargoflowEntityUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .build();
    }

    @Bean("cargoflowListOrderWebClient")
    public WebClient cargoflowListOrderWebClient() throws SSLException {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(cargoflowListOrderUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .build();
    }

    @Bean("cargoflowUploadWebClient")
    public WebClient cargoflowUploadWebClient() throws SSLException {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(cargoflowUploadUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .build();
    }

    @Bean("cargoflowAttachWebClient")
    public WebClient cargoflowAttachWebClient() throws SSLException {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient()))
                .baseUrl(cargoflowAttachUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                })
                .build();
    }

}
