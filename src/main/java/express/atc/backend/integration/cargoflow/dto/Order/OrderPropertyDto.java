package express.atc.backend.integration.cargoflow.dto.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderPropertyDto(
        OrderParticipant buyer,
        OrderParcel parcel,
        OrderParticipant sender,
        Integer bizType,
        String epOrderId,
//     String returnParcel,
        Integer parcelPickupType
) {
}
