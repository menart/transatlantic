package express.atc.backend.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SmsAeroRequest {

    private String number;
    private String text;
    private String sign;
}
