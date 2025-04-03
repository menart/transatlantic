package express.atc.backend.config;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.SSLException;

@Configuration
public class HttClientConfig {

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
}
