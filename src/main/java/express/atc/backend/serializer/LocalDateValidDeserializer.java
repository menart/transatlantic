package express.atc.backend.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class LocalDateValidDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        if (Objects.isNull(jsonParser) || Objects.isNull(jsonParser.getText())) return null;
        try {
            return LocalDate.parse(jsonParser.getText());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
