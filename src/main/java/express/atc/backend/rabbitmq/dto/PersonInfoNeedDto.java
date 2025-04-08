package express.atc.backend.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonInfoNeedDto {

    private String logisticsOrderCode;
    private String trackingNumber;
    private String status;
}
