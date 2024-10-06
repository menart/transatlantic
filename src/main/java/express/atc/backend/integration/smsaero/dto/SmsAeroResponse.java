package express.atc.backend.integration.smsaero.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SmsAeroResponse {
    private boolean success;
    private String message;
}
