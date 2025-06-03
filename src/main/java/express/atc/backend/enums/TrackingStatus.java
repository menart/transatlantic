package express.atc.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TrackingStatus {
    NEED_PAYMENT(true),
    NEED_DOCUMENT(true),
    PAYMENT_CONFIRMATION(true),
    ACTIVE(false),
    ARCHIVE(false);

    private final boolean isNeedAction;
}
