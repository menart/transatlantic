package express.atc.backend.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Objects;

public class MoneyDeserializer extends JsonDeserializer<Long> {
    @Override
    public Long deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (Objects.isNull(jsonParser) || Objects.isNull(jsonParser.getText())) return null;
        String value = jsonParser.getText().replaceAll(" ","");
        return (long) Double.parseDouble(value) * 100;
    }
}
