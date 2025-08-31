package express.atc.backend.integration.cbrf.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Configuration
public class CbrfFeignConfig {

    @Bean
    public Decoder cbrfFeignDecoder() {
        return new JacksonDecoder(new XmlMapper());
    }

    @Bean
    public Encoder cbrfFeignEncoder() {
        return new JacksonEncoder(new XmlMapper());
    }

    @Bean
    public ErrorDecoder cbrfErrorDecoder() {
        return (methodKey, response) -> {
            String errorMessage = String.format("CBRF API request failed: %s %s",
                    response.status(), response.body());
            log.error(errorMessage);
            return new ResponseStatusException(
                    HttpStatus.valueOf(response.status()),
                    errorMessage
            );
        };
    }
}