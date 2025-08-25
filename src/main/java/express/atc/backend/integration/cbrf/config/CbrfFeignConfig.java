package express.atc.backend.integration.cbrf.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}