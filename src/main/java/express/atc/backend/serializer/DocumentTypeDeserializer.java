package express.atc.backend.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import express.atc.backend.enums.DocumentType;

import java.io.IOException;
import java.util.Objects;

public class DocumentTypeDeserializer extends JsonDeserializer<DocumentType> {

    @Override
    public DocumentType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return Objects.nonNull(jsonParser) && Objects.nonNull(jsonParser.getText())
                ? DocumentType.getDocumentTypeByName(jsonParser.getText())
                : null;
    }
}
