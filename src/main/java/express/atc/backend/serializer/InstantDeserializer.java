package express.atc.backend.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class InstantDeserializer extends JsonDeserializer<Instant> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return Objects.nonNull(jsonParser) && Objects.nonNull(jsonParser.getText())
                ? LocalDateTime.parse(jsonParser.getValueAsString(), DATE_TIME_FORMATTER)
                .atOffset(ZoneOffset.UTC)
                .toInstant()
                : null;

    }
}
