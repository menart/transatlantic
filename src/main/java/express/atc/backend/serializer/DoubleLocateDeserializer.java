package express.atc.backend.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Objects;

public class DoubleLocateDeserializer extends JsonDeserializer<Double> {
    @Override
    public Double deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (Objects.isNull(jsonParser) || Objects.isNull(jsonParser.getText())) return null;
        return Double.parseDouble(jsonParser.getText().trim().replace(',', '.'));
    }
}
