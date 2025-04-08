package express.atc.backend.integration.cargoflow.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.integration.cargoflow.dto.Order.OrderPropertyDto;

import java.io.IOException;
import java.util.Objects;

public class CargoflowPropertyDeserializer extends JsonDeserializer<OrderPropertyDto> {

    @Override
    public OrderPropertyDto deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return Objects.nonNull(jsonParser) && Objects.nonNull(jsonParser.getText())
                ? objectMapper.readValue(jsonParser.getText(), OrderPropertyDto.class)
                : null;
    }
}
