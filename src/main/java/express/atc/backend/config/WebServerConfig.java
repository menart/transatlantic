package express.atc.backend.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WebServerConfig {
    private final Environment env;

    public WebServerConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        boolean isHttpEnabled = env.getProperty("server.http.enabled", Boolean.class, false);
        boolean isHttpsEnabled = env.getProperty("server.https.enabled", Boolean.class, false);

        if (isHttpEnabled && isHttpsEnabled) {
            throw new IllegalStateException("Нельзя одновременно включать HTTP и HTTPS");
        }

        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.setPort(env.getProperty("server.https.port", Integer.class, 8080));
        if (isHttpsEnabled) {
            configureSsl(factory);
        }

        return factory;
    }

    private void configureSsl(TomcatServletWebServerFactory factory) {
        Ssl ssl = new Ssl();
        ssl.setKeyStore(env.getProperty("server.https.ssl.key-store"));
        ssl.setKeyStorePassword(env.getProperty("server.https.ssl.key-store-password"));
        ssl.setKeyStoreType(env.getProperty("server.https.ssl.key-store-type"));
        ssl.setKeyAlias(env.getProperty("server.https.ssl.key-alias"));
        factory.setSsl(ssl);
    }
}
