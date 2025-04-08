package express.atc.backend.enums.convert;

import express.atc.backend.enums.TrackingStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class StringToEnumConverter implements Converter<String, TrackingStatus> {
    @Override
    public TrackingStatus convert(String source) {
        try {
            return TrackingStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}