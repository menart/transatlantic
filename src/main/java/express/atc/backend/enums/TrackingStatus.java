package express.atc.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TrackingStatus {
    NEED_PAYMENT,
    FIRST_NEED_DOCUMENT,
    NEED_DOCUMENT,
    PAYMENT_CONFIRMATION,
    ACTIVE,
    ARCHIVE
}
