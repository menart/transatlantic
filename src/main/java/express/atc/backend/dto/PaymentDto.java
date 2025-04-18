package express.atc.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaymentDto {

    private List<PaymentItemDto> items;
    private long orderId;
    private long amount;
    private String trackingNumber;
    private String email;
}
