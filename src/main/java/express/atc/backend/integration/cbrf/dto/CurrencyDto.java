package express.atc.backend.integration.cbrf.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import express.atc.backend.serializer.DoubleLocateDeserializer;
import lombok.Builder;
import lombok.Data;

public record CurrencyDto (

    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id,
    @JacksonXmlProperty(localName = "NumCode")
    String numCode,
    @JacksonXmlProperty(localName = "CharCode")
    String charCode,
    @JacksonXmlProperty(localName = "Nominal")
    Integer nominal,
    @JacksonXmlProperty(localName = "Name")
    String name,
    @JacksonXmlProperty(localName = "Value")
    @JsonDeserialize(using = DoubleLocateDeserializer.class)
    Double value,
    @JacksonXmlProperty(localName = "VunitRate")
    @JsonDeserialize(using = DoubleLocateDeserializer.class)
    Double valueUnitRate){
}
