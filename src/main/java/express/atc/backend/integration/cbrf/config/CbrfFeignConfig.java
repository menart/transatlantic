package express.atc.backend.integration.cbrf.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import feign.RequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableFeignClients(basePackages = "express.atc.backend.integration.cbrf.client")
public class CbrfFeignConfig {

    @Bean
    public Decoder feignDecoder() {
        return new JacksonDecoder(new XmlMapper());
    }

    @Bean
    public Encoder feignEncoder() {
        return new JacksonEncoder(new XmlMapper());
    }

    @Bean
    public RequestInterceptor cbrfRequestInterceptor() {
        return template -> {
            template.header("Accept", MediaType.APPLICATION_XML_VALUE);
            template.header("Accept-Charset", StandardCharsets.UTF_8.name());
        };
    }
}