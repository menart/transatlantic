package express.atc.backend.integration.smsaero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsAeroLoginDto {
    private boolean success;
    private Object data;
    private String message;
}
