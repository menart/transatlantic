package express.atc.backend.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import express.atc.backend.enums.DocumentType;

import java.io.IOException;

public class DocumentTypeSerializer extends JsonSerializer<DocumentType> {


    @Override
    public void serialize(DocumentType documentType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(documentType.getId());
    }
}
