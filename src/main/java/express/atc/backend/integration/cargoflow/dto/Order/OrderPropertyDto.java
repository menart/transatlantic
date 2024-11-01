package express.atc.backend.integration.cargoflow.dto.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
public class OrderPropertyDto {

    private OrderParticipant buyer;
    private OrderParcel parcel;
    private OrderParticipant sender;
    private Integer bizType;
    private String epOrderId;
//    private String returnParcel;
    private Integer parcelPickupType;

}
