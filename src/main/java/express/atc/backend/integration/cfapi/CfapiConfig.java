package express.atc.backend.integration.cfapi;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class CfapiConfig {

    @Value("${cfapi.url}")
    private String cfApiUrl;

    @Value("${cfapi.platformId}")
    private String platformId;

    private final HttpClient httpClient;

    @Bean("cfApiWebClient")
    public WebClient cfApiWebClient() throws SSLException {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(cfApiUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    httpHeaders.set("msgId", UUID.randomUUID().toString());
                    httpHeaders.set("platformId", platformId);
                })
                .build();
    }
}
