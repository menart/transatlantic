package express.atc.backend.integration.cbrf.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import express.atc.backend.serializer.DoubleLocateDeserializer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyDto {

    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    private String id;
    @JacksonXmlProperty(localName = "NumCode")
    private String numCode;
    @JacksonXmlProperty(localName = "CharCode")
    private String charCode;
    @JacksonXmlProperty(localName = "Nominal")
    private Integer nominal;
    @JacksonXmlProperty(localName = "Name")
    private String name;
    @JacksonXmlProperty(localName = "Value")
    @JsonDeserialize(using = DoubleLocateDeserializer.class)
    private Double value;
    @JacksonXmlProperty(localName = "VunitRate")
    @JsonDeserialize(using = DoubleLocateDeserializer.class)
    private Double valueUnitRate;
}
