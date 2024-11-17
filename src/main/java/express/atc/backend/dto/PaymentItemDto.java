package express.atc.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentItemDto {

    private String name;
    private Long quantity;
    private long amount;
}
