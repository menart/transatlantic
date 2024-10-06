package express.atc.backend.integration.smsaero.config;


import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.Base64;
import java.util.Collections;

@Configuration
public class SmsAeroConfig {

    @Value("${sms-aero.token}")
    private String smsAeroToken;
    @Value("${sms-aero.login}")
    private String smsAeroLogin;
    @Value("${sms-aero.url}")
    private String smsAeroUrl;

    @Bean
    public WebClient smsAeroWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();
        HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(smsAeroUrl)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    httpHeaders.setBasicAuth(smsAeroLogin, smsAeroToken);
                })
                .build();
    }

    private static String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        return Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }
}
