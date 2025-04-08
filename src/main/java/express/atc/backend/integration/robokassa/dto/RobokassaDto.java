package express.atc.backend.integration.robokassa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.UriBuilder;
import org.yaml.snakeyaml.util.UriEncoder;

@Slf4j
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public class RobokassaDto {

    private String merchantLogin;
    private Double outSum;
    private String description;
    private Long invId;
    private String email;
    private ReceiptDto receipt;
    private String signatureValue;
    private Integer isTest;

    public UriBuilder getUribuilder(UriBuilder builder) {
        builder.queryParam("MerchantLogin", merchantLogin);
        builder.queryParam("OutSum", outSum);
        builder.queryParam("Description", description);
        builder.queryParam("InvId", invId);
        builder.queryParam("Email", email);
        builder.queryParam("Receipt", UriEncoder.encode(receipt.toString()));
        if (isTest != null && isTest == 1) {
            builder.queryParam("IsTest", "1");
        }
        builder.queryParam("SignatureValue", signatureValue);
        return builder;
    }
}
