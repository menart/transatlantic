package express.atc.backend.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper jsonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true);
        objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        objectMapper.setConfig(objectMapper.getSerializationConfig()
                .with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        javaTimeModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
}
