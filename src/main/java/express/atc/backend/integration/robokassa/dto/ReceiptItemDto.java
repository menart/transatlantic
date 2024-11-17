package express.atc.backend.integration.robokassa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import express.atc.backend.integration.robokassa.enums.RobokassaPaymentObject;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReceiptItemDto {

    //    @JsonSerialize(using = Utf8PHPSerializer.class)
    private String name;
    private Double quantity;
    private Double cost;
    private Double sum;
    private String paymentMethod;
    private RobokassaPaymentObject paymentObject;
    private String tax;
    private String nomenclatureCode;
}
